package info.loenwind.enderioaddons.machine.ihopper;

import static crazypants.enderio.config.Config.powerConduitTierOneRF;
import static crazypants.enderio.config.Config.powerConduitTierThreeRF;
import static crazypants.enderio.config.Config.powerConduitTierTwoRF;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import static info.loenwind.enderioaddons.EnderIOAddons.mode24;
import static info.loenwind.enderioaddons.config.Config.impulseHopperRFusePerItem;
import static info.loenwind.enderioaddons.config.Config.impulseHopperRFusePerOperation;
import static info.loenwind.enderioaddons.config.Config.impulseHopperWorkEveryTick1;
import static info.loenwind.enderioaddons.config.Config.impulseHopperWorkEveryTick2;
import static info.loenwind.enderioaddons.config.Config.impulseHopperWorkEveryTick3;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.EnderIOAddons;
import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.gui.AdvancedRedstoneMode;
import info.loenwind.enderioaddons.gui.IAdvancedRedstoneModeControlable;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileIHopper extends AbstractTileFramework implements IFrameworkMachine, IAdvancedRedstoneModeControlable {

  static final int SLOTS = 6;

  @Store
  private RedstoneModeState redstoneModeState = new RedstoneModeState();

  public TileIHopper() {
    super(new GhostlySlotDefinition(SLOTS, SLOTS, 1, SLOTS));
  }

  @Override
  public String getMachineName() {
    return BlockIHopper.ModObject_blockIHopper.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      int ipos = i - slotDefinition.getMinInputSlot(); // 0-5
      int gpos = ((GhostlySlotDefinition) slotDefinition).getMinGhostSlot() + ipos;
      return ItemUtil.areStacksEqual(itemstack, getStackInSlot(gpos));
    } else {
      return false;
    }
  }

  @Override
  public boolean isActive() {
    return hasPower() && (redstoneCheckPassed || hasBeenTriggered);
  }

  /*
   * We override the redstone mode handling here.
   * 
   * The inherited redstoneCheckPassed is set to the current value.
   * 
   * hasBeenTriggered is set to true if redstoneCheckPassed is true and is reset
   * after work has been done. This allows a short pulse between two
   * shouldDoWorkThisTick()s to have an effect.
   * 
   * recomputeState is the ack from processTasks() for hasBeenTriggered. It is
   * needed so that state triggers can re-enable hasBeenTriggered. For pulse and
   * edge triggers this does nothing.
   * 
   * requiresResyncForChangedRSMode imitates the superclass' client syncing on
   * redstone change.
   */
  private Field redstoneStateDirty = null;
  @Store({ SAVE })
  private boolean requiresResyncForChangedRSMode = false;
  @Store({ SAVE })
  private boolean hasBeenTriggered = false;
  @Store({ SAVE })
  private boolean recomputeState = false;

  @Override
  public void doUpdate() {    
    if(!worldObj.isRemote) {
      try {
        if (redstoneStateDirty == null) {
          redstoneStateDirty = AbstractMachineEntity.class.getDeclaredField("redstoneStateDirty");
          redstoneStateDirty.setAccessible(true);
        }
        if (recomputeState || redstoneStateDirty.getBoolean(this)) {
          recomputeState = false;
          boolean prevRedCheck = redstoneCheckPassed;
          redstoneCheckPassed = redstoneModeState.isConditionMet(getWorldObj(), getLocation());
          hasBeenTriggered = hasBeenTriggered || redstoneCheckPassed;
          redstoneStateDirty.set(this, Boolean.FALSE);
          requiresResyncForChangedRSMode = prevRedCheck != redstoneCheckPassed;
        }
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
        Log.error(e.getMessage());
      }
    }
    super.doUpdate();
  }

  @Override
  protected boolean processTasks(boolean rsCheckPassed) {
    if (hasBeenTriggered && shouldDoWorkThisTick(tickSpeedFromCap())) {
      boolean result = processTasksImpl() || requiresResyncForChangedRSMode;
      hasBeenTriggered = false;
      recomputeState = true;
      requiresResyncForChangedRSMode = false;
      return result;
    } else if (requiresResyncForChangedRSMode) {
      requiresResyncForChangedRSMode = false;
      return true;
    } else {
      return false;
    }
  }

  private void playSound() {
    if (crazypants.enderio.config.Config.machineSoundsEnabled) {
      getWorldObj().playSoundEffect(xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, EnderIOAddons.DOMAIN + ":machine.ihopper", getVolume(), getPitch());
    }
  }

  /**
   * Check if the an input slot satisfies its ghost slot.
   * 
   * @param slot
   *          The slot to check (1..6)
   * @return true if the input slots satisfies the requirement, false otherwise.
   */
  public boolean checkInputSlot(int slot) {
    final ItemStack ghostSlot = ghostSlot(slot);
    final ItemStack inputSlot = inputSlot(slot);
    return ghostSlot != null && inputSlot != null && ItemUtil.areStacksEqual(ghostSlot, inputSlot) && ghostSlot.stackSize <= inputSlot.stackSize;
  }

  /**
   * Check if the a ghost slot is active.
   * 
   * @param slot
   *          The slot to check (1..6)
   * @return true if the ghost slots is filled, false otherwise.
   */
  public boolean checkGhostSlot(int slot) {
    final ItemStack ghostSlot = ghostSlot(slot);
    return ghostSlot != null;
  }

  /**
   * Check if the an output slot can take an operation's output.
   * 
   * @param slot
   *          The slot to check (1..6)
   * @return true if the output slots has space, false otherwise.
   */
  public boolean checkOutputSlot(int slot) {
    final ItemStack ghostSlot = ghostSlot(slot);
    final ItemStack outputSlot = outputSlot(slot);
    return outputSlot == null
        || (ghostSlot != null && ItemUtil.areStackMergable(ghostSlot, outputSlot) && outputSlot.stackSize + ghostSlot.stackSize <= outputSlot.getMaxStackSize());
  }

  private float getPowerNeedForSlot(int slot) {
    final ItemStack ghostSlot = ghostSlot(slot);
    return ghostSlot != null ? ghostSlot.stackSize * impulseHopperRFusePerItem.getFloat() : 0f;
  }

  private boolean processTasksImpl() {
    if (usePower(impulseHopperRFusePerOperation.getInt())) {
      // (1) Check if we can do a copy operation
      float neededPower = 0;
      boolean doSomething = false;
      for (int slot = 1; slot <= SLOTS; slot++) {
        if (checkGhostSlot(slot)) {
          if (checkInputSlot(slot) && checkOutputSlot(slot)) {
            doSomething = true;
            neededPower += getPowerNeedForSlot(slot);
          } else {
            // We cannot, one of the preconditions is false
            return false;
          }
        }
      }
      // (2) Abort if there is nothing to copy or we don't have enough power 
      if (!doSomething || !usePower(neededPower)) {
        return false;
      }
      // (3) Do the copy. Skip all the checks done above
      for (int slot = 1; slot <= SLOTS; slot++) {
        final ItemStack ghostSlot = ghostSlot(slot);
        final ItemStack inputSlot = inputSlot(slot);
        if (ghostSlot != null && inputSlot != null) {
          final ItemStack outputSlot = outputSlot(slot);
          if (outputSlot != null) {
            final ItemStack result = outputSlot.copy();
            result.stackSize += ghostSlot.stackSize;
            setInventorySlotContents(outputSlotNo(slot), result);
          } else {
            final ItemStack result = inputSlot.copy();
            result.stackSize = ghostSlot.stackSize;
            setInventorySlotContents(outputSlotNo(slot), result);
          }
          if (ghostSlot.stackSize < inputSlot.stackSize) {
            final ItemStack remainder = inputSlot.copy();
            remainder.stackSize -= ghostSlot.stackSize;
            setInventorySlotContents(inputSlotNo(slot), remainder);
          } else {
            setInventorySlotContents(inputSlotNo(slot), null);
          }
        }
      }
      playSound();
      return true;
    } else {
      return false;
    }
  }

  int ghostSlotNo(int no) {
    return no + ((GhostlySlotDefinition) slotDefinition).minGhostSlot - 1;
  }

  @Nullable
  ItemStack ghostSlot(int no) {
    return inventory[ghostSlotNo(no)];
  }

  int outputSlotNo(int no) {
    return no + slotDefinition.minOutputSlot - 1;
  }

  @Nullable
  ItemStack outputSlot(int no) {
    return inventory[outputSlotNo(no)];
  }

  int inputSlotNo(int no) {
    return no + slotDefinition.minInputSlot - 1;
  }

  @Nullable
  ItemStack inputSlot(int no) {
    return inventory[inputSlotNo(no)];
  }

  public boolean canUsePower(Float wantToUse) {
    int w = wantToUse.intValue();
    return !((w < 1 ? 1 : w) > getEnergyStored());
  }

  public boolean canUsePower(int wantToUse) {
    return !(wantToUse > getEnergyStored());
  }

  public boolean usePower(Float wantToUse) {
    int w = wantToUse.intValue();
    return usePower(w < 1 ? 1 : w);
  }

  public boolean usePower(int wantToUse) {
    if (mode24) {
      return true;
    } else if (wantToUse > getEnergyStored()) {
      return false;
    } else {
      setEnergyStored(getEnergyStored() - wantToUse);
      return true;
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(powerConduitTierOneRF, 100000, powerConduitTierOneRF));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(powerConduitTierTwoRF, 200000, powerConduitTierTwoRF));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(powerConduitTierThreeRF, 500000, powerConduitTierThreeRF));
      break;
    }
  }

  private int tickSpeedFromCap() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return impulseHopperWorkEveryTick1.getInt();
    case ACTIVATED_CAPACITOR:
      return impulseHopperWorkEveryTick2.getInt();
    case ENDER_CAPACITOR:
      return impulseHopperWorkEveryTick3.getInt();
    }
    return 0;
  }

  @Override
  public void setGhostSlotContents(int slot, ItemStack contents) {
    if (((GhostlySlotDefinition) getSlotDefinition()).isGhostSlot(slot)) {
      super.setInventorySlotContents(slot, contents);
      forceClientUpdate = true;
    }
  }

  @Override
  public boolean hasTank(@Nonnull TankSlot tankSlot) {
    // render no tanks (lower level)
    return false;
  }

  @Override
  public Fluid getTankFluid(@Nonnull TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
    case BACK_RIGHT:
    case FRONT_RIGHT:
    case BACK_LEFT:
    }
    return null;
  }

  @Override
  public boolean hasController() {
    return true;
  }

  @Override
  public String getControllerModelName() {
    return BlockIHopper.blockIHopper.getControllerModelName();
  }

  @Override
  public boolean renderSlot(@Nonnull TankSlot tankSlot) {
    return false; // is handled by our renderer
  }

  @Override
  public IIcon getSlotIcon(@Nonnull TankSlot tankSlot, int side) {
    return null;
  }

  @Override
  public AdvancedRedstoneMode getAdvancedRedstoneControlMode() {
    return redstoneModeState.getMode();
  }

  @Override
  public void setAdvancedRedstoneControlMode(AdvancedRedstoneMode mode) {
    redstoneModeState.setMode(mode);
    // use side effect of setting rs state dirty:
    setRedstoneControlMode(getRedstoneControlMode());
  }

}

package info.loenwind.enderioaddons.machine.voidtank;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.baseclass.TileEnderIOAddons;
import info.loenwind.enderioaddons.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.SmartTank;

@Storable
public class TileVoidTank extends TileEnderIOAddons implements IFluidHandler, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  private static int IO_MB_TICK = 100;

  @Nonnull
  @Store
  protected SmartTank tank = new SmartTank(16 * ONE_BLOCK_OF_LIQUID * 32 / 48);

  protected int lastUpdateLevel = -1;
  protected boolean tankDirty = false;

  public TileVoidTank() {
    super(new SlotDefinition(1, 1, 1));
  }

  @Override
  protected boolean doPush(@Nullable ForgeDirection dir) {
    return false;
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {

    if (isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPull(dir);
    if (tank.getFluidAmount() < tank.getCapacity()) {
      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if(target != null) {

        if (tank.getFluidAmount() > 0) {
          FluidStack canPull = tank.getFluid().copy();
          canPull.amount = tank.getCapacity() - tank.getFluidAmount();
          canPull.amount = Math.min(canPull.amount, IO_MB_TICK);
          FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
          if (drained != null && drained.amount > 0) {
            tank.fill(drained, true);
            tankDirty = true;
            return res;
          }
        } else {

          FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
          if (infos != null) {
            for (FluidTankInfo info : infos) {
              if (info.fluid != null && info.fluid.amount > 0) {
                if (canFill(dir, info.fluid.getFluid())) {
                  FluidStack canPull = info.fluid.copy();
                  canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
                  FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
                  if (drained != null && drained.amount > 0) {
                    tank.fill(drained, true);
                    tankDirty = true;
                    return res;
                  }
                }
              }
            }
          }
        }

      }
    }
    return res;
  }

  @Override
  public int fill(@Nullable ForgeDirection from, @Nullable FluidStack resource, boolean doFill) {
    if (!canFill(from) || resource == null || !isValidFluid(resource)) {
      return 0;
    }
    return fillInternal(resource, doFill);
  }

  protected boolean canFill(ForgeDirection from) {
    IoMode mode = getIoMode(from);
    return mode != IoMode.PUSH && mode != IoMode.DISABLED;
  }

  protected int fillInternal(@Nonnull FluidStack resource, boolean doFill) {
    int res = tank.fill(resource, doFill);
    if(res > 0 && doFill) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(@Nullable ForgeDirection from, @Nullable FluidStack resource, boolean doDrain) {
    return null;
  }

  protected FluidStack drainInternal(@Nonnull FluidStack resource, boolean doDrain) {
    FluidStack res = tank.drain(resource, doDrain);
    if(res != null && res.amount > 0 && doDrain) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(@Nullable ForgeDirection from, int maxDrain, boolean doDrain) {
    return null;
  }

  protected FluidStack drainInternal(int maxDrain, boolean doDrain) {
    FluidStack res = tank.drain(maxDrain, doDrain);
    if(res != null && res.amount > 0 && doDrain) {
      tankDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(@Nullable ForgeDirection from, @Nullable Fluid fluid) {
    return canFill(from) && fluid != null && (tank.getFluidAmount() > 0 && tank.getFluid().getFluidID() == fluid.getID() || tank.getFluidAmount() == 0)
        && isValidFluid(fluid);
  }

  @Override
  public boolean canDrain(@Nullable ForgeDirection from, @Nullable Fluid fluid) {
    return false;
  }

  @Override
  public FluidTankInfo[] getTankInfo(@Nullable ForgeDirection from) {
    return new FluidTankInfo[] { new FluidTankInfo(tank) };
  }

  protected int getFilledLevel() {
    int level = (int) Math.floor(16 * tank.getFilledRatio());
    if(level == 0 && tank.getFluidAmount() > 0) {
      level = 1;
    }
    return level;
  }

  @Override
  public String getMachineName() {
    return BlockVoidTank.ModObject_blockVoidTank.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, @Nullable ItemStack item) {
    if (i == 0 && item != null) {
      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(item);
      if (fluid != null) {
        return isValidFluid(fluid);
      }
      if (item.getItem() == Items.water_bucket) {
        return true;
      }
      if (item.getItem() == Items.lava_bucket) {
        return true;
      }
      if (item.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) item.getItem()).getFluid(item) != null) {
        return isValidFluid(((IFluidContainerItem) item.getItem()).getFluid(item));
      }
      return false;
    }
    return false;
  }

  @SuppressWarnings({ "static-method", "unused" })
  protected boolean isValidFluid(Fluid fluid) {
    return true;
  }

  @SuppressWarnings({ "static-method", "unused" })
  protected boolean isValidFluid(FluidStack fluid) {
    return true;
  }

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if(canTick(redstoneChecksPassed) && redstoneChecksPassed) {
      return doTick();
    }
    return false;
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if(redstoneChecksPassed) {
      if(getEnergyStored() < getPowerUsePerTick()) {
        return false;
      } else if (tank.getFluidAmount() > 0) {
        usePower();
        return true;
      }
    }
    return false;
  }

  protected boolean doTick() {
    if(shouldDoWorkThisTick(20)) {
      drainFullContainer();
    }

    // scale by cap
    int modulo = 10;
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      modulo = 20;
      break;
    case ACTIVATED_CAPACITOR:
      modulo = 10;
      break;
    case ENDER_CAPACITOR:
      modulo = 2;
      break;
    }

    if (shouldDoWorkThisTick(modulo) && tank.getFluidAmount() > 0) {
      int amount = tank.getFluidAmount() - 100;
      if (amount < 0) {
        amount = 0;
      }
      tank.setFluidAmount(amount);
    }

    int filledLevel = getFilledLevel();
    if (lastUpdateLevel != filledLevel) {
      lastUpdateLevel = filledLevel;
      tankDirty = true;
    }

    if (tankDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketVoidTank(this), this);
      worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
      tankDirty = false;
    }

    return false;
  }

  private boolean drainFullContainer() {
    FluidAndStackResult fill = FluidUtil.tryDrainContainer(inventory[getSlotDefinition().getMinInputSlot()], this);
    if (fill.result.fluidStack == null) {
      return false;
    }

    int slot = getSlotDefinition().getMinOutputSlot();

    if (inventory[slot] != null && fill.result.itemStack != null) {
      if (inventory[slot].isStackable() && ItemUtil.areStackMergable(inventory[slot], fill.result.itemStack)
          && inventory[slot].stackSize < inventory[slot].getMaxStackSize()) {
        fill.result.itemStack.stackSize += inventory[slot].stackSize;
      } else {
        return false;
      }
    }

    getInputTank(fill.result.fluidStack).setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(getSlotDefinition().getMinInputSlot(), fill.remainder.itemStack);
    if (fill.result.itemStack != null) {
      setInventorySlotContents(slot, fill.result.itemStack);
    }

    setTanksDirty();
    markDirty();
    return false;
  }

  @Override
  protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return createTask(null);
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR: // TODO cfg
      setCapacitor(new BasicCapacitor(Config.niardContinuousEnergyUseRF.getInt() * 40, 250000, Config.niardContinuousEnergyUseRF.getInt()));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.niardContinuousEnergyUseRF.getInt() * 40, 500000, Config.niardContinuousEnergyUseRF.getInt()));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.niardContinuousEnergyUseRF.getInt() * 40, 1000000, Config.niardContinuousEnergyUseRF.getInt()));
      break;
    }
    currentTask = createTask(null);
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return isValidFluid(forFluidType) ? tank : null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] {};
  }

  @Override
  public void setTanksDirty() {
    tankDirty = true;
  }

}

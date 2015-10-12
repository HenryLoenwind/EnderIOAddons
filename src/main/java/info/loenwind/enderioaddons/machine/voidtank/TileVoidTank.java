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

  @Store
  protected boolean isBroken = false;

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
    return doTick(redstoneChecksPassed);
  }

  protected boolean doTick(boolean redstoneChecksPassed) {
    if (redstoneChecksPassed && shouldDoWorkThisTick(20)) {
      drainFullContainer();
    }
    if (isBroken) {
      usePower();
    }

    if (tank.getFluidAmount() > 0) {
      // scale by cap
      int drainPerTick = 10;
      switch (getCapacitorType()) {
      case BASIC_CAPACITOR:
        drainPerTick = 100 / 20;
        break;
      case ACTIVATED_CAPACITOR:
        drainPerTick = 100 / 10;
        break;
      case ENDER_CAPACITOR:
        drainPerTick = 100 / 2;
        break;
      }
      int amount = tank.getFluidAmount() - getWorldObj().rand.nextInt(drainPerTick * 2);
      if (amount < 0) {
        amount = 0;
      }
      tank.setFluidAmount(amount);
      if (!isBroken) {
        isBroken = true;
        getWorldObj().createExplosion(null, xCoord, yCoord, zCoord, 1, true);
        return true;
      }
    }

    if (shouldDoWorkThisTick(10)) {
      if (tankDirty || lastUpdateLevel != tank.getFluidAmount()) {
        PacketHandler.sendToAllAround(new PacketVoidTank(this), this);
        lastUpdateLevel = tank.getFluidAmount();
        tankDirty = false;
      }
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
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.voidTankContinuousEnergyUseRF1.getInt() * 10, 100000, Config.voidTankContinuousEnergyUseRF1.getInt()));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.voidTankContinuousEnergyUseRF2.getInt() * 5, 500000, Config.voidTankContinuousEnergyUseRF2.getInt()));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.voidTankContinuousEnergyUseRF3.getInt() * 2, 1000000, Config.voidTankContinuousEnergyUseRF3.getInt()));
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

  public boolean isBroken() {
    return isBroken;
  }

}

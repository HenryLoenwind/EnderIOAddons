package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.common.Fluids;
import info.loenwind.enderioaddons.machine.framework.AbstractTileFramework;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.SmartTank;

public class TileWaterworks extends AbstractTileFramework implements IFrameworkMachine, IProgressTile, IFluidHandler, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  protected SmartTank tank1 = new SmartTank(3 * ONE_BLOCK_OF_LIQUID);
  protected SmartTank tank2 = new SmartTank(1 * ONE_BLOCK_OF_LIQUID);

  private static int IO_MB_TICK = 100;

  boolean tanksDirty = false;

  protected float progress = 0;
  protected Fluid progress_in = Fluids.BRINE1.getFluid();
  protected Fluid progress_out = Fluids.BRINE2.getFluid();

  public TileWaterworks() {
    super(new SlotDefinition(0, 14, 1));

    tank1.setFluid(new FluidStack(Fluids.BRINE1.getFluid(), 1500));
    tank2.setFluid(new FluidStack(Fluids.BRINE2.getFluid(), 150));

  }

  protected int outputSlotNo(int no) {
    return no + slotDefinition.minOutputSlot;
  }

  @Override
  public String getMachineName() {
    return BlockWaterworks.ModObject_blockWaterworks.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    if (redstoneCheckPassed) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(crazypants.enderio.config.Config.powerConduitTierOneRF, 100000, crazypants.enderio.config.Config.powerConduitTierOneRF));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(crazypants.enderio.config.Config.powerConduitTierTwoRF, 200000, crazypants.enderio.config.Config.powerConduitTierTwoRF));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(crazypants.enderio.config.Config.powerConduitTierThreeRF, 500000,
          crazypants.enderio.config.Config.powerConduitTierThreeRF));
      break;
    }
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
  }

  @Override
  public boolean hasTank(TankSlot tankSlot) {
    return true;
  }

  @Override
  public Fluid getTankFluid(TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
      return tank1.getFluidAmount() > 0 ? tank1.getFluid().getFluid() : null;
    case BACK_RIGHT:
      return tank1.getFluidAmount() > 2000 ? tank1.getFluid().getFluid() : null;
    case FRONT_RIGHT:
      return tank2.getFluidAmount() > 0 ? tank2.getFluid().getFluid() : null;
    case BACK_LEFT:
      return tank1.getFluidAmount() > 1000 ? tank1.getFluid().getFluid() : null;
    }
    return null;
  }

  @Override
  public boolean hasController() {
    return true;
  }

  @Override
  public boolean renderSlot(TankSlot tankSlot) {
    return tankSlot != TankSlot.FRONT_LEFT;
  }

  @Override
  public IIcon getSlotIcon(TankSlot tankSlot, int side) {
    return BlockWaterworks.blockWaterworks.filterTexture;
  }

  @Override
  public String getControllerModelName() {
    return BlockWaterworks.blockWaterworks.getControllerModelName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public float getProgress() {
    // TODO Auto-generated method stub
    progress += 1.0f / 20 / 15;
    if (progress > 1) {
      progress = 0;
    }
    return progress;
  }

  @Override
  public void setProgress(float progress) {
    // TODO Auto-generated method stub

  }

  @Override
  public TileEntity getTileEntity() {
    return this;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && tank1.canFill(forFluidType.getFluid())) {
      return tank1;
    }
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { tank2 };
  }

  @Override
  public void setTanksDirty() {
    tanksDirty = true;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if (isSideDisabled(from.ordinal())) {
      return 0;
    }

    if (resource == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    int res = tank1.fill(resource, doFill);
    if (res > 0 && doFill) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if (isSideDisabled(from.ordinal())) {
      return null;
    }
    if (tank2.getFluid() == null || resource == null || !resource.isFluidEqual(tank2.getFluid())) {
      return null;
    }
    FluidStack res = tank2.drain(resource.amount, doDrain);
    if (res != null && res.amount > 0 && doDrain) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    if (isSideDisabled(from.ordinal())) {
      return null;
    }
    FluidStack res = tank2.drain(maxDrain, doDrain);
    if (res != null && res.amount > 0 && doDrain) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if (isSideDisabled(from.ordinal()) || fluid == null || !tank1.canFill(fluid)) {
      return false;
    }

    int flid = fluid.getID();
    return flid == FluidRegistry.WATER.getID() || flid == Fluids.BRINE1.getFluid().getID() || flid == Fluids.BRINE2.getFluid().getID()
        || flid == Fluids.BRINE3.getFluid().getID() || flid == Fluids.BRINE4.getFluid().getID();
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if (isSideDisabled(from.ordinal())) {
      return false;
    }
    return tank2.canDrainFluidType(fluid);
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    if (isSideDisabled(from.ordinal())) {
      return new FluidTankInfo[0];
    }
    return new FluidTankInfo[] { tank1.getInfo(), tank2.getInfo() };
  }

  @Override
  protected boolean doPush(ForgeDirection dir) {
    if (isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPush(dir);
    if (tank2.getFluidAmount() > 0) {

      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if (target != null) {
        if (target.canFill(dir.getOpposite(), tank2.getFluid().getFluid())) {
          FluidStack push = tank2.getFluid().copy();
          push.amount = Math.min(push.amount, IO_MB_TICK);
          int filled = target.fill(dir.getOpposite(), push, true);
          if (filled > 0) {
            tank2.drain(filled, true);
            tanksDirty = true;
            return res;
          }
        }
      }

    }
    return res;
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {
    if (isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPull(dir);
    if (!tank1.isFull()) {
      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if (target != null) {
        if (tank1.getFluidAmount() > 0) {
          FluidStack canPull = tank1.getFluid().copy();
          canPull.amount = tank1.getCapacity() - tank1.getFluidAmount();
          canPull.amount = Math.min(canPull.amount, IO_MB_TICK);
          FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
          if (drained != null && drained.amount > 0) {
            tank1.fill(drained, true);
            tanksDirty = true;
            return res;
          }
        } else {
          //empty input tank
          FluidTankInfo[] infos = target.getTankInfo(dir.getOpposite());
          if (infos != null) {
            for (FluidTankInfo info : infos) {
              if (info.fluid != null && info.fluid.amount > 0) {
                if (canFill(dir, info.fluid.getFluid())) {
                  FluidStack canPull = info.fluid.copy();
                  canPull.amount = Math.min(IO_MB_TICK, canPull.amount);
                  FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
                  if (drained != null && drained.amount > 0) {
                    tank1.fill(drained, true);
                    tanksDirty = true;
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

}

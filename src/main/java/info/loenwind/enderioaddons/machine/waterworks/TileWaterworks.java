package info.loenwind.enderioaddons.machine.waterworks;

import info.loenwind.enderioaddons.common.Fluids;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.waterworks.engine.ConfigProvider;
import info.loenwind.enderioaddons.machine.waterworks.engine.Engine;
import info.loenwind.enderioaddons.machine.waterworks.engine.Engine.CreationResult;
import info.loenwind.enderioaddons.machine.waterworks.engine.Stash;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.SmartTank;

public class TileWaterworks extends AbstractPoweredTaskEntity implements IFrameworkMachine, IProgressTile, IFluidHandler, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  protected SmartTank tank1 = new SmartTank(3 * ONE_BLOCK_OF_LIQUID);
  protected SmartTank tank2 = new SmartTank(1 * ONE_BLOCK_OF_LIQUID);

  private static int IO_MB_TICK = 100;

  boolean tanksDirty = false;

  protected Fluid progress_in = null;
  protected Fluid progress_out = null;

  protected static final Engine engine = new Engine(ConfigProvider.readConfig());
  protected final Stash stash = new Stash();

  protected static ColMap data;

  public TileWaterworks() {
    super(new SlotDefinition(0, 14, 1));

    if (data == null) {
      int amount = ONE_BLOCK_OF_LIQUID * Config.waterWorksWaterReductionPercentage / 100;
      data = new ColMap(5);
      data.set(0, FluidRegistry.WATER, Fluids.BRINE1, 0, amount);
      data.set(1, Fluids.BRINE1, Fluids.BRINE2, 1, amount);
      data.set(2, Fluids.BRINE2, Fluids.BRINE3, 2, amount);
      data.set(3, Fluids.BRINE3, Fluids.BRINE4, 3, amount);
      data.set(4, Fluids.BRINE4, 4);
    }
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

    if (tank2.getFluidAmount() == 0) {
      return data.containsInputLiquid(fluid);
    } else {
      return data.isMatchingInputForOutput(fluid, tank2.getFluid().getFluid());
    }
  }

  private static boolean flequals(Fluid a, Fluid b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.getID() == b.getID();
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

  @Override
  protected boolean hasInputStacks() {
    // used by super class to determine if it should try to start a new task
    return tank1.getFluidAmount() >= ONE_BLOCK_OF_LIQUID
        || engine.createItems(stash, this, slotDefinition.minOutputSlot, slotDefinition.maxOutputSlot, false) == CreationResult.OK;
  }

  @Override
  protected IMachineRecipe getNextRecipe() {
    return DummyRecipe.instance;
  }

  // read as canStartNextTask()
  @Override
  protected boolean canInsertResult(float chance, IMachineRecipe nextRecipe) {
    progress_in = tank1.getFluidAmount() > 0 ? tank1.getFluid().getFluid() : null;
    progress_out = tank2.getFluidAmount() > 0 ? tank2.getFluid().getFluid() : null;

    if (tank1.getFluidAmount() < ONE_BLOCK_OF_LIQUID) {
      return false;
    }

    int level = data.getLevelFromInput(tank1.getFluid().getFluid());
    if (level < 0) {
      return false;
    }
    if (tank2.getAvailableSpace() < data.getOutputAmountFromInput(tank1.getFluid().getFluid())) {
      return false;
    }
    if (!data.isMatchingOutputForInput(tank1.getFluid().getFluid(), tank2.getFluidAmount() > 0 ? tank2.getFluid().getFluid() : null)) {
      return false;
    }

    progress_out = data.getOutputFromInput(tank1.getFluid().getFluid());

    return engine.createItems(stash, this, slotDefinition.minOutputSlot, slotDefinition.maxOutputSlot, false) == CreationResult.OK;
  }

}

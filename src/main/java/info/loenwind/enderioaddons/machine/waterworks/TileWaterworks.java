package info.loenwind.enderioaddons.machine.waterworks;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import static info.loenwind.enderioaddons.common.NullHelper.notnullF;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.HandleStash;
import info.loenwind.enderioaddons.common.Fluids;
import info.loenwind.enderioaddons.common.TileEnderIOAddons;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.framework.IFrameworkMachine;
import info.loenwind.enderioaddons.machine.waterworks.engine.ConfigProvider;
import info.loenwind.enderioaddons.machine.waterworks.engine.Engine;
import info.loenwind.enderioaddons.machine.waterworks.engine.Engine.CreationResult;
import info.loenwind.enderioaddons.machine.waterworks.engine.Stash;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.SmartTank;

@Storable
public class TileWaterworks extends TileEnderIOAddons implements IFrameworkMachine, IFluidHandler, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  @Nonnull
  @Store
  protected SmartTank inputTank = new SmartTank(3 * ONE_BLOCK_OF_LIQUID);
  @Nonnull
  @Store
  protected SmartTank outputTank = new SmartTank(1 * ONE_BLOCK_OF_LIQUID);

  private static int IO_MB_TICK = 200;

  boolean tanksDirty = false;

  @Nullable
  @Store({ SAVE, CLIENT })
  protected Fluid progress_in = null;
  @Nullable
  @Store({ SAVE, CLIENT })
  protected Fluid progress_out = null;

  @Nonnull
  protected static final Engine engine = new Engine(ConfigProvider.readConfig());
  @Nonnull
  @Store(value = { SAVE, ITEM }, handler = HandleStash.class)
  protected final Stash stash = new Stash();
  @Store({ SAVE, ITEM, CLIENT })
  protected float stashProgress = 0.0f;

  protected static ColMap data;

  public TileWaterworks() {
    super(new SlotDefinition(0, 14, 1));

    if (data == null) {
      int amount = ONE_BLOCK_OF_LIQUID * Config.waterWorksWaterReductionPercentage / 100;
      data = new ColMap(5);
      data.set(0, notnullF(FluidRegistry.WATER, "FluidRegistry.WATER"), Fluids.BRINE1, 0, amount);
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
      setCapacitor(new BasicCapacitor(Config.waterWorksRFinPerTick1, 100000, Config.waterWorksRFusePerTick1));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.waterWorksRFinPerTick2, 200000, Config.waterWorksRFusePerTick2));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.waterWorksRFinPerTick3, 500000, Config.waterWorksRFusePerTick3));
      break;
    }
  }

  protected float getLiquidFactorPerTask() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return (float) Config.waterWorksLiquidFactorperTask1;
    case ACTIVATED_CAPACITOR:
      return (float) Config.waterWorksLiquidFactorperTask2;
    case ENDER_CAPACITOR:
      return (float) Config.waterWorksLiquidFactorperTask3;
    }
    return 0;
  }

  protected float getRfPerTask() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return (float) Config.waterWorksRFperTask1;
    case ACTIVATED_CAPACITOR:
      return (float) Config.waterWorksRFperTask2;
    case ENDER_CAPACITOR:
      return (float) Config.waterWorksRFperTask3;
    }
    return 0;
  }

  @Override
  public boolean hasTank(@Nonnull TankSlot tankSlot) {
    return true;
  }

  @Override
  public Fluid getTankFluid(@Nonnull TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
      return inputTank.getFluidAmount() > 0 ? inputTank.getFluid().getFluid() : null;
    case BACK_LEFT:
      return inputTank.getFilledRatio() > .34 ? inputTank.getFluid().getFluid() : null;
    case BACK_RIGHT:
      return inputTank.getFilledRatio() > .67 ? inputTank.getFluid().getFluid() : null;
    case FRONT_RIGHT:
      return outputTank.getFluidAmount() > 0 ? outputTank.getFluid().getFluid() : null;
    }
    return null;
  }

  @Override
  public boolean hasController() {
    return true;
  }

  @Override
  public boolean renderSlot(@Nonnull TankSlot tankSlot) {
    return tankSlot != TankSlot.FRONT_LEFT;
  }

  @Override
  public IIcon getSlotIcon(@Nonnull TankSlot tankSlot, int side) {
    return BlockWaterworks.blockWaterworks.getFilterTexture();
  }

  @Override
  public String getControllerModelName() {
    return BlockWaterworks.blockWaterworks.getControllerModelName();
  }

  @Override
  @Nullable
  public FluidTank getInputTank(@Nullable FluidStack forFluidType) {
    if (forFluidType != null && inputTank.canFill(forFluidType.getFluid())) {
      return inputTank;
    }
    return null;
  }

  @Override
  @Nonnull
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { outputTank };
  }

  @Override
  public void setTanksDirty() {
    tanksDirty = true;
  }

  @Override
  public int fill(@Nullable ForgeDirection from, @Nullable FluidStack resource, boolean doFill) {
    if (from == null || isSideDisabled(from.ordinal()) || resource == null || !canFill(from, resource.getFluid())) {
      return 0;
    }
    int res = inputTank.fill(resource, doFill);
    if (res > 0 && doFill) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  @Nullable
  public FluidStack drain(@Nullable ForgeDirection from, @Nullable FluidStack resource, boolean doDrain) {
    if (from == null || isSideDisabled(from.ordinal()) || outputTank.getFluid() == null || resource == null || !resource.isFluidEqual(outputTank.getFluid())) {
      return null;
    }
    FluidStack res = outputTank.drain(resource.amount, doDrain);
    if (res != null && res.amount > 0 && doDrain) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  @Nullable
  public FluidStack drain(@Nullable ForgeDirection from, int maxDrain, boolean doDrain) {
    if (from == null || isSideDisabled(from.ordinal())) {
      return null;
    }
    FluidStack res = outputTank.drain(maxDrain, doDrain);
    if (res != null && res.amount > 0 && doDrain) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(@Nullable ForgeDirection from, @Nullable Fluid fluid) {
    if (from == null || isSideDisabled(from.ordinal()) || fluid == null || !inputTank.canFill(fluid)) {
      return false;
    }

    if (outputTank.getFluidAmount() == 0) {
      return data.containsInputLiquid(fluid);
    } else {
      return data.isMatchingInputForOutput(fluid, outputTank.getFluid().getFluid());
    }
  }

  @Override
  public boolean canDrain(@Nullable ForgeDirection from, @Nullable Fluid fluid) {
    if (from == null || isSideDisabled(from.ordinal())) {
      return false;
    }
    return outputTank.canDrainFluidType(fluid);
  }

  @Override
  @Nonnull
  public FluidTankInfo[] getTankInfo(@Nullable ForgeDirection from) {
    if (from == null || isSideDisabled(from.ordinal())) {
      return new FluidTankInfo[0];
    }
    return new FluidTankInfo[] { inputTank.getInfo(), outputTank.getInfo() };
  }

  @Override
  protected boolean doPush(@Nullable ForgeDirection dir) {
    if (dir == null || isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPush(dir);
    if (outputTank.getFluidAmount() > 0) {

      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if (target != null) {
        if (target.canFill(dir.getOpposite(), outputTank.getFluid().getFluid())) {
          FluidStack push = outputTank.getFluid().copy();
          push.amount = Math.min(push.amount, IO_MB_TICK);
          int filled = target.fill(dir.getOpposite(), push, true);
          if (filled > 0) {
            outputTank.drain(filled, true);
            tanksDirty = true;
            return res;
          }
        }
      }

    }
    return res;
  }

  @Override
  protected boolean doPull(@Nullable ForgeDirection dir) {
    if (dir == null || isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPull(dir);
    if (!inputTank.isFull()) {
      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if (target != null) {
        if (inputTank.getFluidAmount() > 0) {
          FluidStack canPull = inputTank.getFluid().copy();
          canPull.amount = inputTank.getCapacity() - inputTank.getFluidAmount();
          canPull.amount = Math.min(canPull.amount, IO_MB_TICK);
          FluidStack drained = target.drain(dir.getOpposite(), canPull, true);
          if (drained != null && drained.amount > 0) {
            inputTank.fill(drained, true);
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
                    inputTank.fill(drained, true);
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
    boolean result = inputTank.getFluidAmount() >= ONE_BLOCK_OF_LIQUID * getLiquidFactorPerTask();
    return result;
  }

  @Override
  @Nonnull
  protected IMachineRecipe getNextRecipe() {
    return DummyRecipe.instance;
  }

  // read as canStartNextTask(), only called after hasInputStacks()
  @Override
  protected boolean canInsertResult(float chance, @Nullable IMachineRecipe nextRecipe) {
    if (inputTank.getFluidAmount() < ONE_BLOCK_OF_LIQUID * getLiquidFactorPerTask()) {
      return false;
    }
    progress_in = inputTank.getFluid().getFluid();

    int level = data.getLevelFromInput(inputTank.getFluid().getFluid());
    if (level < 0) {
      return false;
    }
    if (outputTank.getAvailableSpace() < data.getOutputAmountFromInput(progress_in) * getLiquidFactorPerTask()) {
      return false;
    }
    if (!data.isMatchingOutputForInput(progress_in, outputTank.getFluidAmount() > 0 ? outputTank.getFluid().getFluid() : null)) {
      return false;
    }

    progress_out = data.getOutputFromInput(progress_in);

    return true;
  }

  @Override
  @Nonnull
  protected IPoweredTask createTask(@Nullable IMachineRecipe nextRecipe, float chance) {
    return new TaskWaterworks(getRfPerTask(), getLiquidFactorPerTask());
  }

  @Override
  @Nullable
  protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
    return TaskWaterworks.readFromNBT(taskTagCompound);
  }

  @Override
  protected void drainInputFluid(@Nullable MachineRecipeInput fluid) {
    inputTank.drain((int) (((TaskWaterworks) currentTask).getLiquidInFactor() * ONE_BLOCK_OF_LIQUID), true);
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (currentTask == null || !hasPower()) {
      return false;
    }
    if (redstoneChecksPassed && !currentTask.isComplete()) {
      usePower();
    }
    // then check if we are done
    if (currentTask.isComplete()) {
      taskComplete();
      return true;
    }
    if (tanksDirty) {
      tanksDirty = false;
      return true;
    }

    return false;
  }

  protected boolean insertingIntoSelf = false;

  @Override
  protected boolean isMachineItemValidForSlot(int i, @Nullable ItemStack itemstack) {
    return insertingIntoSelf && i >= slotDefinition.minOutputSlot && i <= slotDefinition.maxOutputSlot;
  }

  @Override
  protected void taskComplete() {
    if (currentTask != null) {
      int level = data.getLevelFromInput(progress_in);
      insertingIntoSelf = true;
      CreationResult creationResult = engine.createItems(stash, level, this, slotDefinition.minOutputSlot, slotDefinition.maxOutputSlot, true);
      insertingIntoSelf = false;
      if (creationResult == CreationResult.OK || creationResult == CreationResult.NO_INPUTS) {
        stashProgress = (float) engine.getLastProgress();
        float factor = ((TaskWaterworks) currentTask).getLiquidInFactor();
        final int amount = (int) (data.getOutputAmountFromInput(progress_in) * factor);
        if (amount > 0) {
          Fluid progress_out2 = progress_out;
          if (progress_out2 == null) {
            progress_out2 = data.getOutputFromInput(progress_in);
          }
          if (progress_out2 != null && outputTank.getFluidAmount() == 0) {
            outputTank.setFluid(new FluidStack(progress_out2, amount));
          } else if (progress_out2 == null || outputTank.getFluid().getFluidID() == progress_out2.getID()) {
            outputTank.addFluidAmount(amount);
          } else {
            return;
          }
        }
        engine.processWater(stash, level, factor);
        currentTask = null;
        progress_in = progress_out = null;
        lastProgressScaled = 0;
        markDirty();
      } else if (creationResult == CreationResult.LOW_OUTPUTS) {
        markDirty();
      }
    }
  }

  @Override
  protected void sendTaskProgressPacket() {
    PacketHandler.sendToAllAround(new PacketWaterworksProgress(this), this);
    ticksSinceLastProgressUpdate = 0;
  }

}

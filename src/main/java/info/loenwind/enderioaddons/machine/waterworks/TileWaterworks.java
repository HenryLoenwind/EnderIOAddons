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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
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

import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.tool.SmartTank;

public class TileWaterworks extends AbstractPoweredTaskEntity implements IFrameworkMachine, IFluidHandler, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  protected SmartTank inputTank = new SmartTank(3 * ONE_BLOCK_OF_LIQUID);
  protected SmartTank outputTank = new SmartTank(1 * ONE_BLOCK_OF_LIQUID);

  private static int IO_MB_TICK = 200;

  boolean tanksDirty = false;

  protected Fluid progress_in = null;
  protected Fluid progress_out = null;

  protected static final Engine engine = new Engine(ConfigProvider.readConfig());
  protected final Stash stash = new Stash();
  protected float stashProgress = 0.0f;

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
      return Config.waterWorksLiquidFactorperTask1;
    case ACTIVATED_CAPACITOR:
      return Config.waterWorksLiquidFactorperTask2;
    case ENDER_CAPACITOR:
      return Config.waterWorksLiquidFactorperTask3;
    }
    return 0;
  }

  protected float getRfPerTask() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return Config.waterWorksRFperTask1;
    case ACTIVATED_CAPACITOR:
      return Config.waterWorksRFperTask2;
    case ENDER_CAPACITOR:
      return Config.waterWorksRFperTask3;
    }
    return 0;
  }

  @Override
  public boolean hasTank(TankSlot tankSlot) {
    return true;
  }

  @Override
  public Fluid getTankFluid(TankSlot tankSlot) {
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
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && inputTank.canFill(forFluidType.getFluid())) {
      return inputTank;
    }
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { outputTank };
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
    int res = inputTank.fill(resource, doFill);
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
    if (outputTank.getFluid() == null || resource == null || !resource.isFluidEqual(outputTank.getFluid())) {
      return null;
    }
    FluidStack res = outputTank.drain(resource.amount, doDrain);
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
    FluidStack res = outputTank.drain(maxDrain, doDrain);
    if (res != null && res.amount > 0 && doDrain) {
      tanksDirty = true;
    }
    return res;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if (isSideDisabled(from.ordinal()) || fluid == null || !inputTank.canFill(fluid)) {
      return false;
    }

    if (outputTank.getFluidAmount() == 0) {
      return data.containsInputLiquid(fluid);
    } else {
      return data.isMatchingInputForOutput(fluid, outputTank.getFluid().getFluid());
    }
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if (isSideDisabled(from.ordinal())) {
      return false;
    }
    return outputTank.canDrainFluidType(fluid);
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    if (isSideDisabled(from.ordinal())) {
      return new FluidTankInfo[0];
    }
    return new FluidTankInfo[] { inputTank.getInfo(), outputTank.getInfo() };
  }

  @Override
  protected boolean doPush(ForgeDirection dir) {
    if (isSideDisabled(dir.ordinal())) {
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
  protected boolean doPull(ForgeDirection dir) {
    if (isSideDisabled(dir.ordinal())) {
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
  protected IMachineRecipe getNextRecipe() {
    return DummyRecipe.instance;
  }

  // read as canStartNextTask(), only called after hasInputStacks()
  @Override
  protected boolean canInsertResult(float chance, IMachineRecipe nextRecipe) {
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
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return new TaskWaterworks(getRfPerTask(), getLiquidFactorPerTask());
  }

  // TODO:  @Override instead of @SuppressWarnings
  @SuppressWarnings("static-method")
  protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
    return TaskWaterworks.readFromNBT(taskTagCompound);
  }

  @Override
  protected void drainInputFluid(MachineRecipeInput fluid) {
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
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
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
        if (outputTank.getFluidAmount() == 0) {
          outputTank.setFluid(new FluidStack(progress_out, (int) (data.getOutputAmountFromInput(progress_in) * factor)));
        } else if (outputTank.getFluid().getFluidID() == progress_out.getID()) {
          outputTank.addFluidAmount((int) (data.getOutputAmountFromInput(progress_in) * factor));
        } else {
          return;
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

  // custom calls common; reading from itemstack calls common

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    IPoweredTask tmp = currentTask; // TODO
    super.readCustomNBT(nbtRoot);
    currentTask = tmp;
    if (nbtRoot.hasKey("progress_in")) {
      progress_in = FluidRegistry.getFluid(nbtRoot.getInteger("progress_in"));
    } else {
      progress_in = null;
    }
    if (nbtRoot.hasKey("progress_out")) {
      progress_out = FluidRegistry.getFluid(nbtRoot.getInteger("progress_out"));
    } else {
      progress_out = null;
    }
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    if (progress_in != null) {
      nbtRoot.setInteger("progress_in", progress_in.getID());
    }
    if (progress_out != null) {
      nbtRoot.setInteger("progress_out", progress_out.getID());
    }
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);

    if (nbtRoot.hasKey("inputTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("inputTank");
      if (tankRoot != null) {
        inputTank.readFromNBT(tankRoot);
      } else {
        inputTank.setFluid(null);
      }
    } else {
      inputTank.setFluid(null);
    }

    if (nbtRoot.hasKey("outputTank")) {
      NBTTagCompound tankRoot = (NBTTagCompound) nbtRoot.getTag("outputTank");
      if (tankRoot != null) {
        outputTank.readFromNBT(tankRoot);
      } else {
        outputTank.setFluid(null);
      }
    } else {
      outputTank.setFluid(null);
    }

    if (nbtRoot.hasKey("stash")) {
      NBTTagCompound stashRoot = (NBTTagCompound) nbtRoot.getTag("stash");
      stash.readFromNbt(stashRoot);
    }

    stashProgress = nbtRoot.getFloat("stashProgress");
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);

    if (inputTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      inputTank.writeToNBT(tankRoot);
      nbtRoot.setTag("inputTank", tankRoot);
    }

    if (outputTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      outputTank.writeToNBT(tankRoot);
      nbtRoot.setTag("outputTank", tankRoot);
    }

    NBTTagCompound stashRoot = new NBTTagCompound();
    stash.writeToNbt(stashRoot);
    nbtRoot.setTag("stash", stashRoot);

    nbtRoot.setFloat("stashProgress", stashProgress);
  }

  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeCustomNBT(tag);
    // remove non-network-required data
    tag.removeTag("stash");
    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
  }

  @Override
  protected void sendTaskProgressPacket() {
    PacketHandler.sendToAllAround(new PacketWaterworksProgress(this), this);
    ticksSinceLastProgressUpdate = 0;
  }

}

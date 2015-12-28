package info.loenwind.enderioaddons.machine.drain;

import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;
import static info.loenwind.enderioaddons.EnderIOAddons.mode24;
import static info.loenwind.enderioaddons.common.NullHelper.notnull;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.enderioaddons.HandleSetBlockCoord;
import info.loenwind.enderioaddons.baseclass.TileEnderIOAddons;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.fluid.Fluids;
import info.loenwind.enderioaddons.machine.drain.FluidHelper.ReturnObject;
import info.loenwind.enderioaddons.machine.drain.filter.FoodFluidFilter;
import info.loenwind.enderioaddons.machine.drain.filter.NoFilter;
import info.loenwind.enderioaddons.machine.drain.filter.TankFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
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
public class TileDrain extends TileEnderIOAddons implements IFluidHandler, IWaterSensitive, IDrainingCallback, ITankAccess {

  private static final int ONE_BLOCK_OF_LIQUID = 1000;

  private final static @Nonnull FoodFluidFilter filter = new FoodFluidFilter();

  private static int IO_MB_TICK = 100;

  @Nonnull
  @Store
  protected SmartTank tank = new SmartTank(2 * ONE_BLOCK_OF_LIQUID);
  protected int lastUpdateLevel = -1;
  
  private boolean tankDirty = false;

  @Store(value = { SAVE }, handler = HandleSetBlockCoord.class)
  protected Set<BlockCoord> nowater = new HashSet<BlockCoord>();
  protected boolean registered = false;
  protected int dryruncount = 0;

  public TileDrain() {
    super(new SlotDefinition(1, 1, 1));
  }

  public boolean isFoodDrain() {
    return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1;
  }

  @Override
  protected boolean doPush(@Nullable ForgeDirection dir) {

    if (dir == null || isSideDisabled(dir.ordinal())) {
      return false;
    }

    boolean res = super.doPush(dir);
    if(tank.getFluidAmount() > 0) {

      BlockCoord loc = getLocation().getLocation(dir);
      IFluidHandler target = FluidUtil.getFluidHandler(worldObj, loc);
      if(target != null) {
        if(target.canFill(dir.getOpposite(), tank.getFluid().getFluid())) {
          FluidStack push = tank.getFluid().copy();
          push.amount = Math.min(push.amount, IO_MB_TICK);
          int filled = target.fill(dir.getOpposite(), push, true);
          if(filled > 0) {
            tank.drain(filled, true);
            tankDirty = true;
            return res;
          }
        }
      }

    }
    return res;
  }

  @Override
  public int fill(@Nullable ForgeDirection from, @Nullable FluidStack resource, boolean doFill) {
    return 0;
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
    if (!canDrain(from) || resource == null) {
      return null;
    }
    return drainInternal(resource, doDrain);
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
    if(!canDrain(from)) {
      return null;
    }
    return drainInternal(maxDrain, doDrain);
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
    return false;
  }

  @Override
  public boolean canDrain(@Nullable ForgeDirection from, @Nullable Fluid fluid) {
    return canDrain(from) && tank.canDrainFluidType(fluid);
  }

  private boolean canDrain(@Nullable ForgeDirection from) {
    IoMode mode = getIoMode(from);
    return mode != IoMode.PULL && mode != IoMode.DISABLED;
  }
  
  @Override
  public FluidTankInfo[] getTankInfo(@Nullable ForgeDirection from) {
    return new FluidTankInfo[] { new FluidTankInfo(tank) };
  }

  private int getFilledLevel() {
    int level = (int) Math.floor(16 * tank.getFilledRatio());
    if(level == 0 && tank.getFluidAmount() > 0) {
      level = 1;
    }
    return level;
  }

  @Override
  public String getMachineName() {
    return BlockDrain.ModObject_blockDrain.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, @Nullable ItemStack item) {
    if (i == 0 && item != null) {
      return FluidContainerRegistry.isEmptyContainer(item) || item.getItem() == Items.bucket;
    }
    return false;
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
      if (getEnergyStored() < getPowerUsePerTick() && !mode24) {
        return false;
      }
      usePower();
    }
    int curScaled = getProgressScaled(16);
    if(curScaled != lastProgressScaled) {
      sendTaskProgressPacket();
      lastProgressScaled = curScaled;
    }
    return true;
  }

  protected boolean doTick() {
    if(shouldDoWorkThisTick(20)) {
      fillEmptyContainer();
    }

    int filledLevel = getFilledLevel();
    if(lastUpdateLevel != filledLevel) {
      lastUpdateLevel = filledLevel;
      tankDirty = true;
    }

    if(tankDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketDrain(this), this);
      worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
      tankDirty = false;
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
    
    if (shouldDoWorkThisTick(modulo) && tank.getAvailableSpace() >= ONE_BLOCK_OF_LIQUID) {
      FluidHelper instance = null;
      final FluidStack tankfluid = tank.getFluid();
      if (isFoodDrain()) {
        if (Config.foodDrainCollectsFlowingMilk.getBoolean()) {
          if (tankfluid != null) {
            instance = FluidHelper.getInstance(notnull(worldObj, "Invalid game state: World is missing"),
                notnull(getLocation(), "Invalid game state: TE location is missing"), new TankFilter(tank), new NoFilter(false));
          } else {
            instance = FluidHelper.getInstance(notnull(worldObj, "Invalid game state: World is missing"),
                notnull(getLocation(), "Invalid game state: TE location is missing"), filter, new NoFilter(false));
          }
        }
      } else {
        if (tankfluid != null) {
          instance = FluidHelper.getInstance(notnull(worldObj, "Invalid game state: World is missing"),
              notnull(getLocation(), "Invalid game state: TE location is missing"), new TankFilter(tank), filter);
        } else {
          instance = FluidHelper.getInstance(notnull(worldObj, "Invalid game state: World is missing"),
              notnull(getLocation(), "Invalid game state: TE location is missing"), new NoFilter(true), filter);
        }
      }
      if (instance != null) {
        instance.setDrainingCallback(this);
        ReturnObject pullFluid = instance.eatOrPullFluid();
        final FluidStack resultFluid = pullFluid.result;
        if (resultFluid != null) {
          fillInternal(resultFluid, true);
          usePower(Config.drainPerBucketEnergyUseRF.getInt());
        } else if (pullFluid.inProgress) {
          usePower(Config.drainPerSourceBlockMoveEnergyUseRF.getInt());
        }
        dryruncount = 0;
        return true;
      } else {
        if (Config.foodDrainCollectsMilkFromCows.getBoolean() && isFoodDrain() && (tankfluid == null || tankfluid.getFluid() == Fluids.MILK.getFluid())) {
          AxisAlignedBB bb = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord).expand(Config.foodDrainRange.getFloat(), 1.5f,
              Config.foodDrainRange.getFloat());
          List cowsInRange = worldObj.getEntitiesWithinAABB(EntityCow.class, bb);
          for (EntityCow cow : (List<EntityCow>) cowsInRange) {
            if (cow.getClass() == EntityCow.class) {
              fillInternal(new FluidStack(Fluids.MILK.getFluid(), 2), true);
              return true;
            } else {
              // TODO: Mooshrooms, MooCows and other modded cows
            }
          }
        }
        if (dryruncount++ > 60) {
          dryruncount = 0;
          nowater.clear();
          if (registered) {
            InfiniteWaterSourceStopper.getInstance().unregister(notnull(worldObj, "Invalid game state: World is missing"), this);
            registered = false;
          }
        }
      }
    }
    return false;
  }
  
  public int getComparatorOutput() {
    FluidTankInfo info = getTankInfo(null)[0];
    return info == null || info.fluid == null ? 0 : (int) (((double) info.fluid.amount / (double) info.capacity) * 15);
  }

  private boolean fillEmptyContainer() {
    FluidAndStackResult fill = FluidUtil.tryFillContainer(inventory[0], getOutputTanks()[0].getFluid());
    if (fill.result.fluidStack == null) {
      return false;
    }

    if (inventory[1] != null) {
      if (inventory[1].isStackable() && ItemUtil.areStackMergable(inventory[1], fill.result.itemStack)
          && inventory[1].stackSize < inventory[1].getMaxStackSize()) {
        fill.result.itemStack.stackSize += inventory[1].stackSize;
      } else {
        return false;
      }
    }

    getOutputTanks()[0].setFluid(fill.remainder.fluidStack);
    setInventorySlotContents(0, fill.remainder.itemStack);
    setInventorySlotContents(1, fill.result.itemStack);

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
      setCapacitor(new BasicCapacitor(Config.drainContinuousEnergyUseRF.getInt() * 40, 250000, Config.drainContinuousEnergyUseRF.getInt()));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.drainContinuousEnergyUseRF.getInt() * 40, 500000, Config.drainContinuousEnergyUseRF.getInt()));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.drainContinuousEnergyUseRF.getInt() * 40, 1000000, Config.drainContinuousEnergyUseRF.getInt()));
      break;
    }
    currentTask = createTask(null);
  }

  @Override
  public void setWorldObj(@Nullable World p_145834_1_) {
    super.setWorldObj(p_145834_1_);
    if (!worldObj.isRemote && !nowater.isEmpty() && !registered && !isFoodDrain()) {
      // actually part of readCommon(nbt), but the world object is not yet set
      // when that is called
      InfiniteWaterSourceStopper.getInstance().register(notnull(worldObj, "Invalid game state: World is missing"), this);
      registered = true;
    }
  }

  @Override
  public boolean preventInfiniteWaterForming(@Nonnull World world, @Nonnull BlockCoord bc) {
    return nowater.contains(bc);
  }

  @Override
  public void onWaterDrain(@Nonnull World world, @Nonnull BlockCoord bc) {
    if (!worldObj.isRemote && !registered && !isFoodDrain()) {
      InfiniteWaterSourceStopper.getInstance().register(notnull(worldObj, "Invalid game state: World is missing"), this);
		  registered = true;
	  }
	  nowater.add(bc);
  }

  @Override
  public void onWaterDrainNearby(@Nonnull World world, @Nonnull BlockCoord bc) {
	  nowater.add(bc);
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { tank };
  }

  @Override
  public void setTanksDirty() {
    tankDirty = true;
  }

}

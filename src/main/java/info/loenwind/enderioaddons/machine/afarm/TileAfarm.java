package info.loenwind.enderioaddons.machine.afarm;

import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.enderioaddons.config.Config.farmArea1;
import static info.loenwind.enderioaddons.config.Config.farmArea2;
import static info.loenwind.enderioaddons.config.Config.farmArea3;
import static info.loenwind.enderioaddons.config.Config.farmBreaksIronRakesChance;
import static info.loenwind.enderioaddons.config.Config.farmBreaksIronRakesEnabled;
import static info.loenwind.enderioaddons.config.Config.farmBreaksWoodenRakesChance;
import static info.loenwind.enderioaddons.config.Config.farmBreaksWoodenRakesEnabled;
import static info.loenwind.enderioaddons.config.Config.farmDelay1;
import static info.loenwind.enderioaddons.config.Config.farmDelay2;
import static info.loenwind.enderioaddons.config.Config.farmDelay3;
import static info.loenwind.enderioaddons.config.Config.farmRFperTickPerArea;
import static info.loenwind.enderioaddons.config.Config.farmRFperTickPerModule;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.enderioaddons.baseclass.TileEnderIOAddons;
import info.loenwind.enderioaddons.common.Log;
import info.loenwind.enderioaddons.common.Profiler;
import info.loenwind.enderioaddons.config.Config;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.module.CropModule;
import info.loenwind.enderioaddons.machine.afarm.module.CrossBreedModule;
import info.loenwind.enderioaddons.machine.afarm.module.CrossCropModule;
import info.loenwind.enderioaddons.machine.afarm.module.FertilizerModule;
import info.loenwind.enderioaddons.machine.afarm.module.HarvestModule;
import info.loenwind.enderioaddons.machine.afarm.module.IAfarmControlModule;
import info.loenwind.enderioaddons.machine.afarm.module.NSEWmodule;
import info.loenwind.enderioaddons.machine.afarm.module.PlantModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteCropsModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteCrossCropsModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteDestroyModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteFertilizerModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteHarvestingModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecutePlantingModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteTillModule;
import info.loenwind.enderioaddons.machine.afarm.module.execute.ExecuteWeedModule;
import info.loenwind.enderioaddons.machine.niard.RadiusIterator;
import info.loenwind.enderioaddons.machine.part.ItemMachinePart;
import info.loenwind.enderioaddons.machine.part.MachinePart;
import info.loenwind.enderioaddons.network.INetworkUpdatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.api.v1.IMutation;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;

import cpw.mods.fml.common.registry.GameRegistry.ItemStackHolder;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.farm.FakeFarmPlayer;
import crazypants.enderio.machine.farm.TileFarmStation.ToolType;
import crazypants.enderio.power.BasicCapacitor;

@Storable
public class TileAfarm extends TileEnderIOAddons implements INetworkUpdatable {

  @ItemStackHolder(value = "AgriCraft:handRake", meta = 0)
  public static final ItemStack handRake_wood = null;
  @ItemStackHolder(value = "AgriCraft:handRake", meta = 1)
  public static final ItemStack handRake_iron = null;

  public static final int NUM_CONTROL_SLOTS = 6;
  public static final int NUM_CONTROL_STORAGE_SLOTS = 12;
  public static final int NUM_SEED_GHOST_SLOTS = 4;
  public static final int NUM_SEED_STORAGE_SLOTS = 12;
  public static final int NUM_OUTPUT_SLOTS = 18;
  public static final int NUM_TOOL_SLOTS = 2;
  public static final int NUM_FERTILIZER_SLOTS = 3;
  public static final int NUM_CROPSTICK_SLOTS = 6;

  private EntityPlayerMP farmerJoe;

  private static APIv1 agricraft = null;
  private static List<ItemStack> cropSticks;
  private static List<ItemStack> rakes;

  private RadiusIterator itr = null;
  private BlockCoord currentTile = null;

  @Store({ CLIENT })
  public NotifSet notifications = new NotifSet();
  @Store
  public boolean tillAggresively = false;

  public TileAfarm() {
    super(new SlotDefinitionAfarm(NUM_CONTROL_SLOTS, NUM_CONTROL_STORAGE_SLOTS, NUM_SEED_GHOST_SLOTS, NUM_SEED_STORAGE_SLOTS, NUM_OUTPUT_SLOTS, NUM_TOOL_SLOTS,
        NUM_FERTILIZER_SLOTS, NUM_CROPSTICK_SLOTS, 1));
    if (agricraft == null) {
      throw new RuntimeException(
          "Agricraft not installed but trying to create Agricraft Farmer. This is a coding error in Ender IO Addons. Please report this.");
    }
    tillAggresively = Config.farmAggressiveTillingDefault.getBoolean();
  }

  public static void detectAgri() {
    APIBase api = API.getAPI(1);
    if (api.getStatus().isOK() && api.getVersion() == 1) {
      agricraft = (APIv1) api;
      cropSticks = agricraft.getCropsItems();
      rakes = agricraft.getRakeItems();
      AgriDetector.hasAgri = true;
    }
  }

  @Override
  public void init() {
    super.init();
    currentTask = createTask(null);
  }

  private EntityPlayerMP getFarmerJoe() {
    // delayed init
    if (farmerJoe == null || farmerJoe.worldObj != worldObj) {
      farmerJoe = new FakeFarmPlayer(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId));
    }
    return farmerJoe;
  }

  @Override
  public String getMachineName() {
    return BlockAfarm.ModObject_blockAfarm.unlocalisedName;
  }

  /////////////////////////////////////////////////////////////////////
  // Slot handline
  /////////////////////////////////////////////////////////////////////

  @Override
  protected boolean isMachineItemValidForSlot(int slotno, @Nullable ItemStack item) {
    if (item == null || item.getItem() == null) {
      return false;
    }
    switch (((SlotDefinitionAfarm) slotDefinition).getSlotType(slotno)) {
    case CONTROL_STORAGE:
      return item.getItem() instanceof IAfarmControlModuleItem;
    case CROPSTICK:
      for (ItemStack cropStick : cropSticks) {
        if (cropStick.getItemDamage() == OreDictionary.WILDCARD_VALUE && cropStick.getItem() == item.getItem()
            && ItemStack.areItemStackTagsEqual(cropStick, item)) {
          return true;
        } else if (ItemUtil.areStacksEqual(cropStick, item)) {
          return true;
        }
      }
      return false;
    case FERTILIZER:
      return agricraft.isSupportedFertilizer(item);
    case SEED:
      return agricraft.isHandledByAgricraft(item);
    case TOOL:
      if (isRake(item)) {
        return getRakeSlot_silent() == -1;
      } else if (ToolType.HOE.itemMatches(item)) {
        return getHoeSlot_silent() == -1;
      } else {
        return false;
      }
    case CONTROL:
      if (item.getItem() instanceof IAfarmControlModuleItem) {
        IAfarmControlModule candidate = ((IAfarmControlModuleItem) item.getItem()).getWorker(item);
        for (IAfarmControlModule installed : getControlModules()) {
          if (!candidate.isCompatibleWith(installed) || !installed.isCompatibleWith(candidate)) {
            return false;
          }
        }
        return true;
      }
      return false;
    case SEED_GHOST:
    case OUTPUT:
    case UPGRADE:
    default:
      return false;
    }
  }

  @Override
  public void setGhostSlotContents(int slot, ItemStack contents) {
    if (((SlotDefinitionAfarm) slotDefinition).getSlotType(slot) == SLOT.SEED_GHOST && (contents == null || agricraft.isHandledByAgricraft(contents))) {
      super.setInventorySlotContents(slot, contents);
      forceClientUpdate = true;
    }
  }

  /////////////////////////////////////////////////////////////////////
  // Tool handling
  /////////////////////////////////////////////////////////////////////

  private int getHoeSlot_silent() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (ToolType.HOE.itemMatches(inventory[i])) {
        notifications.remove(Notif.NO_HOE);
        return i;
      }
    }
    return -1;
  }

  public int getHoeSlot() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (ToolType.HOE.itemMatches(inventory[i])) {
        notifications.remove(Notif.NO_HOE);
        return i;
      }
    }
    notifications.add(Notif.NO_HOE);
    return -1;
  }

  private int getRakeSlot_silent() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (isRake(inventory[i])) {
        notifications.remove(Notif.NO_RAKE);
        return i;
      }
    }
    return -1;
  }

  public int getRakeSlot() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (isRake(inventory[i]) && !tryToBrakeRake(i)) {
        notifications.remove(Notif.NO_RAKE);
        return i;
      }
    }
    notifications.add(Notif.NO_RAKE);
    return -1;
  }

  private boolean tryToBrakeRake(int i) {
    if (handRake_wood != null && inventory[i].getItem() == handRake_wood.getItem() && inventory[i].getItemDamage() == handRake_wood.getItemDamage()
        && farmBreaksWoodenRakesEnabled.getBoolean() && random.nextDouble() < farmBreaksWoodenRakesChance.getDouble()) {
      if (inventory[i].stackSize > 1) {
        inventory[i].stackSize--;
      } else {
        inventory[i] = null;
      }
      putIntoOutputOrWorld(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.RAKE_BR1.ordinal()));
    } else if (handRake_iron != null && inventory[i].getItem() == handRake_iron.getItem() && inventory[i].getItemDamage() == handRake_iron.getItemDamage()
        && farmBreaksIronRakesEnabled.getBoolean() && random.nextDouble() < farmBreaksIronRakesChance.getDouble()) {
      if (inventory[i].stackSize > 1) {
        inventory[i].stackSize--;
      } else {
        inventory[i] = null;
      }
      putIntoOutputOrWorld(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.RAKE_BR2.ordinal()));
    }
    return inventory[i] == null;
  }

  private void putIntoOutputOrWorld(ItemStack stack) {
    int firstfree = -1;
    if (stack != null) {
      final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) getSlotDefinition();
      for (int oslot = slotDef.getMinSlot(SLOT.OUTPUT); oslot <= slotDef.getMaxSlot(SLOT.OUTPUT); oslot++) {
        final ItemStack ostack = getStackInSlot(oslot);
        if (ostack != null) {
          if (ItemUtil.areStackMergable(stack, ostack) && ostack.stackSize < ostack.getMaxStackSize()) {
            int free = ostack.getMaxStackSize() - ostack.stackSize;
            if (free >= stack.stackSize) {
              ostack.stackSize += stack.stackSize;
              stack.stackSize = 0;
              break;
            } else {
              ostack.stackSize = ostack.getMaxStackSize();
              stack.stackSize -= free;
            }
          }
        } else if (firstfree == -1) {
          firstfree = oslot;
        }
      }
      if (stack.stackSize > 0) {
        if (firstfree != -1) {
          setInventorySlotContents(firstfree, stack);
          stack.stackSize = 0;
        } else {
          getWorldObj().spawnEntityInWorld(new EntityItem(getWorldObj(), xCoord + 0.5, yCoord + 0.9, zCoord + 0.5, stack));
        }
      }
    }
  }

  private static boolean isRake(ItemStack item) {
    if (item == null || item.getItem() == null) {
      return false;
    }
    for (ItemStack rake : rakes) {
      if ((rake.getItemDamage() == OreDictionary.WILDCARD_VALUE || rake.isItemStackDamageable())
          && (rake.getItem() == item.getItem() && ItemStack.areItemStackTagsEqual(rake, item))) {
        return true;
      } else if (ItemUtil.areStacksEqual(rake, item)) {
        return true;
      }
    }
    return item.getItem() == ItemMachinePart.itemMachinePart && item.getItemDamage() == MachinePart.IRAKE.ordinal();
  }

  /////////////////////////////////////////////////////////////////////
  // Power use and capacitors
  /////////////////////////////////////////////////////////////////////

  private int calcPowerUsePerTick() {
    return (int) Math.round(getFarmSize() * farmRFperTickPerArea.getDouble() + controlModuleCount() * farmRFperTickPerModule.getDouble());
  }

  private int controlModuleCount() {
    int count = 0;
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null) {
        count++;
      }
    }
    return count;
  }

  public int getFarmSize() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return farmArea1.getInt();
    case ACTIVATED_CAPACITOR:
      return farmArea2.getInt();
    case ENDER_CAPACITOR:
      return farmArea3.getInt();
    }
    return 0;
  }

  public int getDelay() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return farmDelay1.getInt();
    case ACTIVATED_CAPACITOR:
      return farmDelay2.getInt();
    case ENDER_CAPACITOR:
      return farmDelay3.getInt();
    }
    return Integer.MAX_VALUE;
  }

  @Override
  public void onCapacitorTypeChange() {
    int ppt = calcPowerUsePerTick();
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 10, 25000, ppt));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 50000, ppt));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 80, 100000, ppt));
      break;
    }
    currentTask = createTask(null);
    itr = null;
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return createTask(null);
  }

  @Override
  protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  /////////////////////////////////////////////////////////////////////
  // Ticking
  /////////////////////////////////////////////////////////////////////

  @Override
  protected boolean processTasks(boolean redstoneChecksPassed) {
    if (redstoneChecksPassed) {
      return super.processTasks(redstoneChecksPassed);
    }
    notifications.clear();
    notifications.add(Notif.OFFLINE);
    return notifications.isChanged();
  }

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (canTick()) {
      long id = Profiler.server.start();
      boolean ret = doTick();
      Profiler.server.stop(id, "farm tick");
      return ret;
    }
    return notifications.isChanged();
  }

  protected boolean canTick() {
    notifications.remove(Notif.OFFLINE);
    if (usePower(getPowerUsePerTick()) == 0) {
      notifications.add(Notif.NO_POWER);
      return false;
    } else {
      notifications.remove(Notif.NO_POWER);
      return true;
    }
  }

  protected boolean doTick() {
    if (agricraft != null && getFarmerJoe() != null && shouldDoWorkThisTick(getDelay())) {
      if (shouldDoWorkThisTick(20 * 180)) {
        notifications.clear();
      }
      if (itr == null) {
        itr = new RadiusIterator(getLocation(), getFarmSize());
      }
      int loop = 0;
      while (currentTile == null || currentTile.equals(getLocation()) || !isTileViable(currentTile)) {
        currentTile = itr.next();
        if (loop++ > 20) {
          return notifications.isChanged();
        }
      }
      WorkTile tile = new WorkTile(currentTile, this, agricraft, farmerJoe);
      List<IAfarmControlModule> controls = getControlModules();
      for (IAfarmControlModule control : controls) {
        control.doWork(tile);
        if (Config.farmDebugLoggingEnabled.getBoolean()) {
          Log.info(tile + " after " + control);
        }
      }
      if (!tile.doneSomething) {
        currentTile = itr.next();
      }
    }
    return notifications.isChanged();
  }

  private boolean isTileViable(BlockCoord bc) {
    if (!worldObj.getChunkProvider().chunkExists(bc.x >> 4, bc.z >> 4)) {
      return false;
    }
    if (worldObj.isAirBlock(bc.x, bc.y - 1, bc.z) || !worldObj.isAirBlock(bc.x, bc.y + 1, bc.z)) {
      return false;
    }
    if (worldObj.isAirBlock(bc.x, bc.y, bc.z) || agricraft.isCrops(worldObj, bc.x, bc.y, bc.z)) {
      return true;
    }
    if (tillAggresively && worldObj.getBlock(bc.x, bc.y, bc.z).isReplaceable(worldObj, bc.x, bc.y, bc.z)
        && (worldObj.getBlock(bc.x, bc.y - 1, bc.z) == Blocks.dirt || worldObj.getBlock(bc.x, bc.y - 1, bc.z) == Blocks.grass)
        && FluidRegistry.lookupFluidForBlock(worldObj.getBlock(bc.x, bc.y, bc.z)) == null) {
      return true;
    }
    return false;
  }

  /////////////////////////////////////////////////////////////////////
  // Control module handling
  /////////////////////////////////////////////////////////////////////

  private static final IAfarmControlModuleComparator moduleComperator = new IAfarmControlModuleComparator();
  private static final List<IAfarmControlModule> standardModules = new ArrayList<>();
  static {
    standardModules.add(new NSEWmodule());
    standardModules.add(new HarvestModule());
    standardModules.add(new PlantModule());
    standardModules.add(new CrossCropModule());
    standardModules.add(new CropModule());
    standardModules.add(new FertilizerModule());
    standardModules.add(new ExecuteHarvestingModule());
    standardModules.add(new ExecuteDestroyModule());
    standardModules.add(new ExecutePlantingModule());
    standardModules.add(new ExecuteCropsModule());
    standardModules.add(new ExecuteCrossCropsModule());
    standardModules.add(new ExecuteWeedModule());
    standardModules.add(new ExecuteTillModule());
    standardModules.add(new ExecuteFertilizerModule());
    Collections.sort(standardModules, moduleComperator);
  }

  private List<IAfarmControlModule> getControlModules() {
    List<IAfarmControlModule> result = new ArrayList<>();
    result.addAll(standardModules);
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null && inventory[i].getItem() instanceof IAfarmControlModuleItem) {
        result.add(((IAfarmControlModuleItem) inventory[i].getItem()).getWorker(inventory[i]));
      }
    }
    Collections.sort(result, moduleComperator);
    return result;
  }

  private static class IAfarmControlModuleComparator implements Comparator<IAfarmControlModule> {

    @Override
    public int compare(IAfarmControlModule o1, IAfarmControlModule o2) {
      return Integer.compare(o1.getPriority(), o2.getPriority());
    }

  }

  /////////////////////////////////////////////////////////////////////
  // Slot layout information for the GUI
  /////////////////////////////////////////////////////////////////////

  public boolean twoGhosts() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null && inventory[i].getItem() instanceof IAfarmControlModuleItem
          && ((IAfarmControlModuleItem) inventory[i].getItem()).getWorker(inventory[i]) instanceof CrossBreedModule) {
        return true;
      }
    }
    return false;
  }

  /////////////////////////////////////////////////////////////////////
  // IO mode restriction
  /////////////////////////////////////////////////////////////////////

  @Override
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    if (faceHit != ForgeDirection.DOWN) {
      return mode == IoMode.DISABLED;
    }
    return super.supportsMode(faceHit, mode);
  }

  @Override
  public boolean isSideDisabled(int var1) {
    if (var1 != ForgeDirection.DOWN.ordinal()) {
      return true;
    }
    return super.isSideDisabled(var1);
  }

  @Override
  public IoMode getIoMode(ForgeDirection face) {
    if (face != ForgeDirection.DOWN) {
      return IoMode.DISABLED;
    }
    return super.getIoMode(face);
  }

  @Override
  public void setIoMode(ForgeDirection faceHit, IoMode mode) {
    if (faceHit != ForgeDirection.DOWN) {
      super.setIoMode(faceHit, IoMode.DISABLED);
      return;
    }
    super.setIoMode(faceHit, mode);
  }

  /////////////////////////////////////////////////////////////////////
  // Network communication client to server
  /////////////////////////////////////////////////////////////////////

  @Override
  public void networkUpdate(int id, int data) {
    if (id == 0) {
      tillAggresively = data == 1;
    }
    markDirty();
    notifications.setChanged();
  }

  @Override
  public int getNetworkUpdate(int id) {
    return 0;
  }

  @Override
  public int getNetworkUpdateCount() {
    return 0;
  }

  @Override
  public int getNetworkUpdateCheckInterval() {
    return 0;
  }

  /////////////////////////////////////////////////////////////////////
  // Slot background items for the GUI
  /////////////////////////////////////////////////////////////////////

  private static List<ItemStack> seedCache = new ArrayList<>();
  private static List<ItemStack> produceCache = new ArrayList<>();
  private static List<ItemStack> toolsCache = new ArrayList<>();

  public static List<ItemStack> getModules() {
    return Collections.singletonList(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.FCM_BASE.ordinal()));
  }

  public static List<ItemStack> getFertilizers() {
    return Collections.singletonList(new ItemStack(Items.dye, 1, 15));
  }

  public static List<ItemStack> getCropsticks() {
    return cropSticks;
  }

  public static List<ItemStack> getTools() {
    if (toolsCache.isEmpty()) {
      toolsCache.add(new ItemStack(Items.wooden_hoe));
      toolsCache.add(new ItemStack(Items.stone_hoe));
      toolsCache.add(new ItemStack(Items.iron_hoe));
      toolsCache.add(new ItemStack(Items.golden_hoe));
      toolsCache.add(new ItemStack(Items.diamond_hoe));
      for (ItemStack hoe : crazypants.enderio.config.Config.farmHoes) {
        addToToolsCache(hoe);
      }
      for (ItemStack rake : rakes) {
        addToToolsCache(rake);
      }
      toolsCache.add(new ItemStack(ItemMachinePart.itemMachinePart, 1, MachinePart.IRAKE.ordinal()));
    }
    return toolsCache;
  }

  private static void addToToolsCache(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return;
    }
    for (ItemStack itemStack : toolsCache) {
      if (itemStack.isItemEqual(stack)) {
        return;
      }
    }
    toolsCache.add(stack.copy());
  }

  public static List<ItemStack> getProduce() {
    getSeeds();
    return produceCache;
  }

  public static List<ItemStack> getSeeds() {
    if (seedCache.isEmpty()) {
      IMutation[] registeredMutations = agricraft.getRegisteredMutations();
      for (IMutation mutation : registeredMutations) {
        ItemStack[] parents = mutation.getParents();
        for (ItemStack itemStack : parents) {
          addToSeedCache(itemStack);
        }
        addToSeedCache(mutation.getResult());
      }
    }
    return seedCache;
  }

  private static void addToSeedCache(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return;
    }
    for (ItemStack itemStack : seedCache) {
      if (itemStack.isItemEqual(stack)) {
        return;
      }
    }
    seedCache.add(stack.copy());
    ICropPlant cropPlant = agricraft.getCropPlant(stack);
    ArrayList<ItemStack> fruits = cropPlant.getAllFruits();
    for (ItemStack itemStack : fruits) {
      addToProduceCache(itemStack);
    }
  }

  private static void addToProduceCache(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return;
    }
    for (ItemStack itemStack : produceCache) {
      if (itemStack.isItemEqual(stack)) {
        return;
      }
    }
    produceCache.add(stack.copy());
  }

}

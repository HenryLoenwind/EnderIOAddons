package info.loenwind.enderioaddons.machine.afarm;

import info.loenwind.enderioaddons.baseclass.TileEnderIOAddons;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.niard.RadiusIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.farm.TileFarmStation.ToolType;
import crazypants.enderio.power.BasicCapacitor;

public class TileAfarm extends TileEnderIOAddons {

  public static final int NUM_CONTROL_SLOTS = 6;
  public static final int NUM_CONTROL_STORAGE_SLOTS = 12;
  public static final int NUM_SEED_STORAGE_SLOTS = 12;
  public static final int NUM_OUTPUT_SLOTS = 18;
  public static final int NUM_TOOL_SLOTS = 2;
  public static final int NUM_FERTILIZER_SLOTS = 3;
  public static final int NUM_CROPSTICK_SLOTS = 4;

  private static APIv1 agricraft = null;
  private static List<ItemStack> cropSticks;
  private static List<ItemStack> rakes;

  private RadiusIterator itr;

  public TileAfarm() {
    super(new SlotDefinitionAfarm(NUM_CONTROL_SLOTS, NUM_CONTROL_STORAGE_SLOTS, NUM_SEED_STORAGE_SLOTS, NUM_OUTPUT_SLOTS, NUM_TOOL_SLOTS, NUM_FERTILIZER_SLOTS,
        NUM_CROPSTICK_SLOTS, 1));
    if (agricraft == null) {
      APIBase api = API.getAPI(1);
      if (api.getStatus().isOK() && api.getVersion() == 1) {
        agricraft = (APIv1) api;
        cropSticks = agricraft.getCropsItems();
        rakes = agricraft.getRakeItems();
      }
    }
    itr = new RadiusIterator(getLocation(), getFarmSize());
  }

  @Override
  public void init() {
    super.init();
    currentTask = createTask(null);
  }

  @Override
  public String getMachineName() {
    // TODO Auto-generated method stub
    return null;
  }

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
        if (cropStick.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
          return cropStick.getItem() == item.getItem() && ItemStack.areItemStackTagsEqual(cropStick, item);
        } else {
          return ItemUtil.areStacksEqual(cropStick, item);
        }
      }
      return false;
    case FERTILIZER:
      return agricraft.isSupportedFertilizer(item);
    case SEED:
      return agricraft.isHandledByAgricraft(item);
    case TOOL:
      if (isRake(item)) {
        return getRakeSlot() == -1;
      } else if (ToolType.HOE.itemMatches(item)) {
        return getHoeSlot() == -1;
      } else {
        return false;
      }
    case CONTROL:
      // TODO cross check installed modules
      return item.getItem() instanceof IAfarmControlModuleItem;
    case OUTPUT:
    case UPGRADE:
    default:
      return false;
    }
  }

  private int getHoeSlot() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (ToolType.HOE.itemMatches(inventory[i])) {
        return i;
      }
    }
    return -1;
  }

  private int getRakeSlot() {
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.TOOL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.TOOL); i++) {
      if (isRake(inventory[i])) {
        return i;
      }
    }
    return -1;
  }

  private static boolean isRake(ItemStack item) {
    if (item == null || item.getItem() == null) {
      return false;
    }
    for (ItemStack rake : rakes) {
      if (rake.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
        return rake.getItem() == item.getItem() && ItemStack.areItemStackTagsEqual(rake, item);
      } else {
        return ItemUtil.areStacksEqual(rake, item);
      }
    }
    return false;
  }

  private int calcPowerUsePerTick() {
    return Math.round(getFarmSize() * 1f); // TODO cfg
  }

  public int getFarmSize() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return 3; // TODO cfg
    case ACTIVATED_CAPACITOR:
      return 5;
    case ENDER_CAPACITOR:
      return 7;
    }
    return 0;
  }

  public int getDelay() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      return 20; // TODO cfg
    case ACTIVATED_CAPACITOR:
      return 10;
    case ENDER_CAPACITOR:
      return 3;
    }
    return Integer.MAX_VALUE;
  }

  @Override
  public void onCapacitorTypeChange() {
    int size = getFarmSize();
    int ppt = calcPowerUsePerTick();
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 250000, ppt));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 500000, ppt));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(ppt * 40, 1000000, ppt));
      break;
    }
    currentTask = createTask(null);
    if (size != getFarmSize()) {
      itr = new RadiusIterator(getLocation(), getFarmSize());
    }
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return createTask(null);
  }

  @Override
  protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (canTick(redstoneChecksPassed) && redstoneChecksPassed) {
      return doTick();
    }
    return false;
  }

  protected boolean canTick(boolean redstoneChecksPassed) {
    if (redstoneChecksPassed) {
      if (usePower(getPowerUsePerTick()) == 0) {
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  protected boolean doTick() {
    if (shouldDoWorkThisTick(getDelay())) {
      List<IAfarmControlModule> controls = getControlModules();
      if (controls.isEmpty()) {
        return false;
      }
      WorkTile tile = new WorkTile(itr.next(), this, agricraft);
      for (IAfarmControlModule control : controls) {
        control.doWork(tile);
        if (tile.done) {
          break;
        }
      }
    }
    return false;
  }

  private List<IAfarmControlModule> getControlModules() {
    List<IAfarmControlModule> result = new ArrayList<>();
    for (int i = ((SlotDefinitionAfarm) slotDefinition).getMinSlot(SLOT.CONTROL); i <= ((SlotDefinitionAfarm) slotDefinition).getMaxSlot(SLOT.CONTROL); i++) {
      if (inventory[i] != null && inventory[i].getItem() instanceof IAfarmControlModuleItem) {
        result.add(((IAfarmControlModuleItem) inventory[i].getItem()).getWorker(inventory[i]));
      }
    }
    Collections.sort(result, comp);
    return result;
  }

  private static final Comp comp = new Comp();

  private static class Comp implements Comparator<IAfarmControlModule> {

    @Override
    public int compare(IAfarmControlModule o1, IAfarmControlModule o2) {
      return Integer.compare(o1.getPriority(), o2.getPriority());
    }

  }

}

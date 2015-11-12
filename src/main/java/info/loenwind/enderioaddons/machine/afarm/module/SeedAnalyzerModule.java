package info.loenwind.enderioaddons.machine.afarm.module;

import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm;
import info.loenwind.enderioaddons.machine.afarm.SlotDefinitionAfarm.SLOT;
import info.loenwind.enderioaddons.machine.afarm.WorkTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.enderio.core.common.util.ItemUtil;

public class SeedAnalyzerModule implements IAfarmControlModule {

  @Override
  public void doWork(WorkTile workTile) {
    final SlotDefinitionAfarm slotDef = (SlotDefinitionAfarm) workTile.farm.getSlotDefinition();
    for (int slot = slotDef.getMinSlot(SLOT.SEED); slot <= slotDef.getMaxSlot(SLOT.SEED); slot++) {
      final ItemStack stack = workTile.farm.getStackInSlot(slot);
      ISeedStats seedStats = workTile.agricraft.getSeedStats(stack);
      if (seedStats != null) {
        if (!isAnalyzed(stack)) {
          if (workTile.farm.canUsePower(100)) { // TODO: cfg
            workTile.farm.usePower(100); // TODO cfg
            analyze(stack);
            reStack(workTile, slotDef, slot, stack);
            workTile.farm.markDirty();
            return; // analyze one stack per work only
          } else {
            return;
          }
        }
      }
    }
  }

  private static void reStack(WorkTile workTile, final SlotDefinitionAfarm slotDef, int slot, final ItemStack stack) {
    for (int slot2 = slotDef.getMinSlot(SLOT.SEED); slot2 <= slotDef.getMaxSlot(SLOT.SEED); slot2++) {
      if (slot2 != slot) {
        final ItemStack stack2 = workTile.farm.getStackInSlot(slot2);
        if (ItemUtil.areStackMergable(stack, stack2) && stack2.stackSize < stack2.getMaxStackSize()) {
          int free = stack2.getMaxStackSize() - stack2.stackSize;
          if (free >= stack.stackSize) {
            stack2.stackSize += stack.stackSize;
            stack.stackSize = 0;
            workTile.farm.setInventorySlotContents(slot, null);
            return;
          } else {
            stack2.stackSize = stack2.getMaxStackSize();
            stack.stackSize -= free;
          }
        }
      }
    }
  }

  private static final String growth = "growth";
  private static final String gain = "gain";
  private static final String strength = "strength";
  private static final String analyzed = "analyzed";

  private static void analyze(ItemStack specimen) {
    NBTTagCompound tag;
    if (specimen.hasTagCompound()) {
      tag = specimen.getTagCompound();
      if (tag.hasKey(growth) && tag.hasKey(gain) && tag.hasKey(strength)) {
        tag.setBoolean(analyzed, true);
        return;
      }
    } else {
      tag = new NBTTagCompound();
      specimen.setTagCompound(tag);
    }
    tag.setShort(growth, (short) 1);
    tag.setShort(gain, (short) 1);
    tag.setShort(strength, (short) 1);
    tag.setBoolean(analyzed, true);
  }

  public static boolean isAnalyzed(ItemStack specimen) {
    if (specimen.hasTagCompound()) {
      NBTTagCompound tag = specimen.getTagCompound();
      if (tag.hasKey(analyzed)) {
        return tag.getBoolean(analyzed);
      }
    }
    return false;
  }

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public boolean isCompatibleWith(IAfarmControlModule other) {
    return !(other instanceof SeedAnalyzerModule);
  }

  static boolean isSameSeed(ItemStack seed1, ItemStack seed2) {
    if (seed1.getItem() == seed2.getItem()) {
      if (seed1.getItemDamage() != OreDictionary.WILDCARD_VALUE && seed2.getItemDamage() != OreDictionary.WILDCARD_VALUE && seed1.getItemDamage() != seed2.getItemDamage()) {
        return false;
      } else {
        if (seed1.stackTagCompound == seed2.stackTagCompound || seed1.stackTagCompound == null || seed2.stackTagCompound == null || ItemStack.areItemStackTagsEqual(seed1, seed2)) {
          return true;
        }
        for (Object key : seed1.stackTagCompound.func_150296_c()) {
          if (!growth.equals(key) && !gain.equals(key) && !strength.equals(key) && !analyzed.equals(key)) {
            Object o1 = seed1.stackTagCompound.getTag((String) key);
            Object o2 = seed2.stackTagCompound.getTag((String) key);
            if ((o1 == null) != (o2 == null)) {
              return false;
            }
            if (o1 != null && !o1.equals(o2)) {
              return false;
            }
          }
        }
        for (Object key : seed2.stackTagCompound.func_150296_c()) {
          if (!growth.equals(key) && !gain.equals(key) && !strength.equals(key) && !analyzed.equals(key)) {
            Object o1 = seed1.stackTagCompound.getTag((String) key);
            Object o2 = seed2.stackTagCompound.getTag((String) key);
            if ((o1 == null) != (o2 == null)) {
              return false;
            }
            if (o1 != null && !o1.equals(o2)) {
              return false;
            }
          }
        }
        return true;
      }
    }
    return false;
  }

}

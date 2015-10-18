package info.loenwind.enderioaddons.machine.voidtank;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemRendererVoidTank implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    if(data != null && data.length > 0) {
      renderToInventory(item);
    }
  }

  public static void renderToInventory(ItemStack item) {
    if(item.stackTagCompound != null) {
      TileVoidTank tt = new TileVoidTank();
      tt.readCommon(item.stackTagCompound);
      FluidRendererVoidTank.renderTankFluid(tt, 0f, -0.1f, 0f);
      FluidRendererVoidTank.renderBlockContents(tt, 0f, -0.1f, 0f);
    } else {
      FluidRendererVoidTank.renderBlockContents(null, 0f, -0.1f, 0f);
    }
  }

}

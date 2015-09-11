package info.loenwind.enderioaddons.machine.framework;

import javax.annotation.Nonnull;

import net.minecraft.util.IIcon;
import net.minecraftforge.client.model.obj.GroupObject;

public class GroupObjectWithIcon extends GroupObject {

  @Nonnull
  protected ITextureProvider textureProvider;

  public GroupObjectWithIcon(GroupObject go, @Nonnull ITextureProvider textureProvider) {
    this.name = go.name;
    this.glDrawingMode = go.glDrawingMode;
    this.faces = go.faces;
    this.textureProvider = textureProvider;
  }

  public IIcon getControllerTexture() {
    return textureProvider.getTexture();
  }
}

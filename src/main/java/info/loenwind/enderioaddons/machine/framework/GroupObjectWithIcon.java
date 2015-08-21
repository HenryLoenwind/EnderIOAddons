package info.loenwind.enderioaddons.machine.framework;

import net.minecraft.util.IIcon;
import net.minecraftforge.client.model.obj.GroupObject;

public class GroupObjectWithIcon extends GroupObject {

  protected TextureProvider textureProvider;

  public GroupObjectWithIcon(GroupObject go, TextureProvider textureProvider) {
    this.name = go.name;
    this.glDrawingMode = go.glDrawingMode;
    this.faces = go.faces;
    this.textureProvider = textureProvider;
  }

  public IIcon getControllerTexture() {
    return textureProvider.getTexture();
  }
}

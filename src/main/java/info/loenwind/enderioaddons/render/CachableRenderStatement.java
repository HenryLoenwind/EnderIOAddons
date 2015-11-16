package info.loenwind.enderioaddons.render;

public interface CachableRenderStatement {

  void execute(RenderingContext renderingContext);

  void execute_tesselated(RenderingContext renderingContext);

}

package com.client.github.milkshake.mixin

import net.minecraft.client.render.block.BlockModelRenderer
import net.minecraft.world.BlockRenderView
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.render.model.BakedModel
import net.minecraft.util.math.random.Random

import com.client.github.feature.visual.Xray

import org.lwjgl.opengl.GL11

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 
@Mixin(BlockModelRenderer::class)
abstract class BlockModelRendererMixin {
  @Inject(
    method = ["Lnet/minecraft/client/render/block/BlockModelRenderer;render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V"],
    at = [
      At("HEAD")
    ],
    cancellable = true
  )
  private fun _renderStart(
    world: BlockRenderView, model: BakedModel, state: BlockState,
    pos: BlockPos, matrices: MatrixStack, vertexConsumer: VertexConsumer,
    cull: Boolean, random: Random, seed: Long,
    overlay: Int, cir: CallbackInfo
  ) {
    println("Block render start")
    if (Xray.enabled()) GL11.glDisable(GL11.GL_DEPTH_TEST)
  }

  @Inject(
    method = ["Lnet/minecraft/client/render/block/BlockModelRenderer;render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;JI)V"],
    at = [
      At("TAIL")
    ],
    cancellable = true
  )
  private fun _renderEnd(
    world: BlockRenderView, model: BakedModel, state: BlockState,
    pos: BlockPos, matrices: MatrixStack, vertexConsumer: VertexConsumer,
    cull: Boolean, random: Random, seed: Long,
    overlay: Int, cir: CallbackInfo
  ) {
    if (Xray.enabled()) GL11.glEnable(GL11.GL_DEPTH_TEST)
  }
}

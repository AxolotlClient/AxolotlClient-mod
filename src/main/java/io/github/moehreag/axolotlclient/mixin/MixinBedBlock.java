package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.util.OldBed;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.getOppositePartDirection;

@Mixin(BedBlock.class)
public class MixinBedBlock {

	@Inject(method = "getOutlineShape", at=@At("HEAD"), cancellable = true)
	public void fullBedHitbox(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){

		Direction direction = getOppositePartDirection(state).getOpposite();

		VoxelShape shape= OldBed.getShape(direction);
		if (shape != null) {

			cir.setReturnValue(shape);
			cir.cancel();
		}

	}

}

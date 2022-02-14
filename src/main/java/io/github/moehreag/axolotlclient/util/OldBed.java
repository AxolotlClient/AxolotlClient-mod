package io.github.moehreag.axolotlclient.util;

import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class OldBed {

	protected static final VoxelShape TOP_SHAPE;
	protected static final VoxelShape LEG_4_SHAPE;
	protected static final VoxelShape LEG_3_SHAPE;
	protected static final VoxelShape LEG_2_SHAPE;
	protected static final VoxelShape LEG_1_SHAPE;
	protected static final VoxelShape NORTH_SHAPE;
	protected static final VoxelShape SOUTH_SHAPE;
	protected static final VoxelShape EAST_SHAPE;
	protected static final VoxelShape WEST_SHAPE;

	public static VoxelShape getShape(Direction direction) {
		if (!Axolotlclient.CONFIG.General.fullBed)
			return null;

		return switch (direction) {
			case EAST -> EAST_SHAPE;
			case WEST -> WEST_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case NORTH -> NORTH_SHAPE;
			default -> null;
		};
	}





	static {
		TOP_SHAPE = Block.createCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
		LEG_1_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
		LEG_2_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
		LEG_3_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
		LEG_4_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
		NORTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_3_SHAPE);
		SOUTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_2_SHAPE, LEG_4_SHAPE);
		WEST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_2_SHAPE);
		EAST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_3_SHAPE, LEG_4_SHAPE);
	}


}

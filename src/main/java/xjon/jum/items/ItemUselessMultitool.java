package xjon.jum.items;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xjon.jum.util.UselessConfiguration;


public class ItemUselessMultitool extends ItemTool {
 	
	private static final Set<Block> effectiveBlocks = Sets.newHashSet();
	protected Item.ToolMaterial theToolMaterial;
	
	
	public ItemUselessMultitool(ToolMaterial material) 
	{
		super(5.0F, 5.0F, material, effectiveBlocks);
		this.setMaxDamage(3141);
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
    {
		if (stack.isItemEqual(new ItemStack(Blocks.BEDROCK)))
		{
			return 0.0F;
		}
		else
		{
			return 10.0F;
		}
    }
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if (!worldIn.isRemote)
		{
			EntityPlayer player = (EntityPlayer) entityIn;
			ItemStack equipped = player.getHeldItemMainhand();
			if (equipped == stack)
				{
					player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 5, 0));
				}
		
		}
		else { super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected); }
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
	
	@SuppressWarnings("incomplete-switch")
	 public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	 {
		if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(stack, playerIn, worldIn, pos);
            if (hook != 0) return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;

            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (facing != EnumFacing.DOWN && worldIn.isAirBlock(pos.up()))
            {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
                {
                    this.setBlock(stack, playerIn, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                    return EnumActionResult.SUCCESS;
                }

                if (block == Blocks.DIRT)
                {
                    switch ((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT))
                    {
                        case DIRT:
                            this.setBlock(stack, playerIn, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                            return EnumActionResult.SUCCESS;
                        case COARSE_DIRT:
                            this.setBlock(stack, playerIn, worldIn, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                            return EnumActionResult.SUCCESS;
                    }
                }
            }

            return EnumActionResult.PASS;
        }
	 }
		
	    
	    protected void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state)
	    {
	        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

	        if (!worldIn.isRemote)
	        {
	            worldIn.setBlockState(pos, state, 11);
	            stack.damageItem(1, player);
	        }
	    }

	    static final class SwitchDirtType
	        {
	            static final int[] TYPE_LOOKUP = new int[BlockDirt.DirtType.values().length];
	            static
	            {
	                try
	                {
	                    TYPE_LOOKUP[BlockDirt.DirtType.DIRT.ordinal()] = 1;
	                }
	                catch (NoSuchFieldError var2)
	                {
	                    ;
	                }

	                try
	                {
	                    TYPE_LOOKUP[BlockDirt.DirtType.COARSE_DIRT.ordinal()] = 2;
	                }
	                catch (NoSuchFieldError var1)
	                {
	                    ;
	                }
	            }
	        }
	    
	    @Override
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List toolTip, boolean advanced)
		{
			stack.setStackDisplayName(ChatFormatting.AQUA + "Useless Multi-Tool");
	    	toolTip.add("Can mine pretty much anything");
		}

}

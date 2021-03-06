package twistedgate.immersiveposts;

import java.util.ArrayList;
import java.util.Locale;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import twistedgate.immersiveposts.common.blocks.BlockMetalFence;
import twistedgate.immersiveposts.common.blocks.BlockPost;
import twistedgate.immersiveposts.common.blocks.BlockPostBase;
import twistedgate.immersiveposts.common.items.MetalRods;
import twistedgate.immersiveposts.common.items.MultiMetaItem;
import twistedgate.immersiveposts.enums.EnumPostMaterial;
import twistedgate.immersiveposts.utils.StringUtils;

/**
 * @author TwistedGate
 */
@Mod.EventBusSubscriber(modid=IPOMod.ID)
public class IPOStuff{
	public static final ArrayList<Block> BLOCKS=new ArrayList<>();
	public static final ArrayList<Item> ITEMS=new ArrayList<>();
	
	public static BlockPostBase postBase;
	
	public static BlockFence ironFence;
	public static BlockFence goldFence;
	public static BlockFence copperFence;
	public static BlockFence leadFence;
	public static BlockFence silverFence;
	public static BlockFence nickelFence;
	public static BlockFence constantanFence;
	public static BlockFence electrumFence;
	public static BlockFence uraniumFence;
	
	public static BlockPost woodPost;
	public static BlockPost ironPost;
	public static BlockPost goldPost;
	public static BlockPost copperPost;
	public static BlockPost leadPost;
	public static BlockPost silverPost;
	public static BlockPost nickelPost;
	public static BlockPost constantanPost;
	public static BlockPost electrumPost;
	public static BlockPost uraniumPost;
	public static BlockPost netherPost;
	public static BlockPost aluminiumPost;
	public static BlockPost steelPost;
	public static BlockPost concretePost;
	public static BlockPost leadedConcretePost;
	
	public static MultiMetaItem metalRods;
	
	public static final void initBlocks(){
		postBase=new BlockPostBase();
		
		// =========================================================================
		// Fences
		
		ironFence		=createFence("fence_iron");
		goldFence		=createFence("fence_gold");
		copperFence		=createFence("fence_copper");
		leadFence		=createFence("fence_lead");
		silverFence		=createFence("fence_silver");
		nickelFence		=createFence("fence_nickel");
		constantanFence	=createFence("fence_constantan");
		electrumFence	=createFence("fence_electrum");
		uraniumFence	=createFence("fence_uranium");
		
		// =========================================================================
		// Posts
		
		woodPost			=new BlockPost(Material.WOOD, EnumPostMaterial.WOOD);
		ironPost			=createMetalPost(EnumPostMaterial.IRON);
		goldPost			=createMetalPost(EnumPostMaterial.GOLD);
		copperPost			=createMetalPost(EnumPostMaterial.COPPER);
		leadPost			=createMetalPost(EnumPostMaterial.LEAD);
		silverPost			=createMetalPost(EnumPostMaterial.SILVER);
		nickelPost			=createMetalPost(EnumPostMaterial.NICKEL);
		constantanPost		=createMetalPost(EnumPostMaterial.CONSTANTAN);
		electrumPost		=createMetalPost(EnumPostMaterial.ELECTRUM);
		uraniumPost			=createMetalPost(EnumPostMaterial.URANIUM);
		netherPost			=createRockyPost(EnumPostMaterial.NETHERBRICK);
		aluminiumPost		=createMetalPost(EnumPostMaterial.ALUMINIUM);
		steelPost			=createMetalPost(EnumPostMaterial.STEEL);
		concretePost		=createRockyPost(EnumPostMaterial.CONCRETE);
		leadedConcretePost	=createRockyPost(EnumPostMaterial.CONCRETE_LEADED);
		
		// =========================================================================
		// Items
		
		metalRods=new MetalRods();
		
	}
	
	private static BlockPost createMetalPost(EnumPostMaterial postMat){
		return new BlockPost(Material.IRON, postMat);
	}
	
	private static BlockPost createRockyPost(EnumPostMaterial postMat){
		return new BlockPost(Material.ROCK, postMat);
	}
	
	private static BlockFence createFence(String name){
		return new BlockMetalFence(name);
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		for(Block block:BLOCKS){
			if(block instanceof BlockPost){
				if(!IPOConfig.isEnabled(((BlockPost)block).getPostMaterial())){
					ImmersivePosts.log.info("Block-Registration of {}-Post skipped.", ((BlockPost)block).getPostMaterial());
					continue;
				}
			}
			if(block instanceof BlockMetalFence){
				boolean skip=false;
				for(EnumPostMaterial m:EnumPostMaterial.values()){
					if(block==m.getBlock() && !IPOConfig.isEnabled(m)){
						ImmersivePosts.log.info("Block-Registration of {}-Fence skipped.", m);
						skip=true;break;
					}
				}
				
				if(skip) continue;
			}
			
			event.getRegistry().register(block);
		}
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event){
		ImmersivePosts.log.info("Registering Recipes.");
		
		ComparableItemStack compMoldRod = ApiUtils.createComparableItemStack(new ItemStack(IEContent.itemMold, 1, 2), false);
		for(EnumPostMaterial m:EnumPostMaterial.values()){
			switch(m){
				case WOOD:case NETHERBRICK:case ALUMINIUM:
				case STEEL:case CONCRETE:case CONCRETE_LEADED:
					continue;
				default:
					if(IPOConfig.isEnabled(m)){
						String ogName=m.toString().toLowerCase(Locale.ENGLISH);
						String oreName=StringUtils.upperCaseFirst(ogName);
						
						int meta=-1;
						for(int i=0;i<IPOStuff.metalRods.getSubItemCount();i++){
							if(IPOStuff.metalRods.getName(i).equals("stick_"+ogName)){
								meta=i;
								break;
							}
						}
						
						voidRegDynOreRecipe(event, "fence_"+ogName, new ItemStack(m.getBlock()), new Object[]{
								"ISI",
								"ISI",
								'S', "stick"+oreName,
								'I', "ingot"+oreName,
						});
						
						if(meta!=-1 && m!=EnumPostMaterial.IRON)
							voidRegDynOreRecipe(event, "metal_rods/stick_"+(ogName), new ItemStack(IPOStuff.metalRods, 4, meta), new Object[]{
									"I",
									"I",
									'I', "ingot"+oreName,
							});
						
						if(m!=EnumPostMaterial.IRON)
							MetalPressRecipe.addRecipe(Utils.copyStackWithAmount(IEApi.getPreferredOreStack("stick"+oreName), 2), "ingot"+oreName, compMoldRod, 2400);
					}
			}
		}
	}
	
	private static void voidRegDynOreRecipe(Register<IRecipe> event, String name, ItemStack output, Object... params){
		ResourceLocation group=new ResourceLocation(IPOMod.ID, "recipes");
		ResourceLocation regName=new ResourceLocation(IPOMod.ID, "recipes/"+name);
		
		event.getRegistry().register(new ShapedOreRecipe(group, output, params).setRegistryName(regName));
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		for(Item item:ITEMS){
			if(item instanceof ItemBlock){
				boolean skip=false;
				for(EnumPostMaterial m:EnumPostMaterial.values()){
					if(((ItemBlock)item).getBlock()==m.getBlock() && !IPOConfig.isEnabled(m)){
						ImmersivePosts.log.info("Item-Registration of {}-Fence skipped.", m);
						skip=true;break;
					}
				}
				
				if(skip) continue;
			}
			
			event.getRegistry().register(item);
		}
		
		registerFenceOres();
		registerStickOres();
	}
	
	private static void registerFenceOres(){
		String prefix="fence";
		for(EnumPostMaterial mat:EnumPostMaterial.values()){
			switch(mat){
				case WOOD:case NETHERBRICK:case ALUMINIUM:case STEEL:continue;
				default:{
					if(IPOConfig.isEnabled(mat)){
						OreDictionary.registerOre(prefix+StringUtils.upperCaseFirst(mat.toString()), mat.getBlock());
					}else{
						ImmersivePosts.log.info("Ore-Registration of {}-Fence skipped.", mat.toString());
					}
				}
			}
		}
	}
	
	private static void registerStickOres(){
		String prefix="stick";
		String rep="stick_";
		for(Item item:ITEMS){
			if(item instanceof MetalRods){
				MultiMetaItem mItem=(MultiMetaItem)item;
				for(int i=0;i<mItem.getSubItemCount();i++){
					String name=mItem.getName(i).substring(rep.length());
					
					if(mItem.getName(i).contains(rep) && IPOConfig.isEnabled(EnumPostMaterial.valueOf(name.toUpperCase(Locale.ENGLISH)))){
						String oreName=prefix+StringUtils.upperCaseFirst(name);
						
						OreDictionary.registerOre(oreName, new ItemStack(mItem, 1, i));
					}else{
						ImmersivePosts.log.info("Ore-Registration of {}-Rod skipped.", name);
					}
				}
				break;
			}
		}
	}
}

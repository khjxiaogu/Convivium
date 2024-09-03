package com.khjxiaogu.convivium.util;

import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.util.FloatemStack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

public final class FloatSizedOrCatalystIngredient {
	public static final Codec<Float> NON_NEGATIVE_FLOAT=Codec.FLOAT.validate(t->t>=0?DataResult.success(t):DataResult.error(()->("Value must be non-negative: "+t)));
	public static final Codec<FloatSizedOrCatalystIngredient> FLAT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Ingredient.MAP_CODEC_NONEMPTY.forGetter(FloatSizedOrCatalystIngredient::ingredient),
		NeoForgeExtraCodecs.optionalFieldAlwaysWrite(NON_NEGATIVE_FLOAT, "count", 1f).forGetter(FloatSizedOrCatalystIngredient::count))
		.apply(instance, FloatSizedOrCatalystIngredient::new));

	/**
	 * The "nested" codec for {@link SizedIngredient}.
	 *
	 * <p>
	 * The count is serialized separately from the rest of the ingredient, for
	 * example:
	 *
	 * <pre>{@code
	 * {
	 *     "ingredient": {
	 *         "item": "minecraft:apple"
	 *     },
	 *     "count": 3
	 * }
	 * }</pre>
	 */
	public static final Codec<FloatSizedOrCatalystIngredient> NESTED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FloatSizedOrCatalystIngredient::ingredient),
		NeoForgeExtraCodecs.optionalFieldAlwaysWrite(NON_NEGATIVE_FLOAT, "count", 1f).forGetter(FloatSizedOrCatalystIngredient::count))
		.apply(instance, FloatSizedOrCatalystIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FloatSizedOrCatalystIngredient> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC,
		FloatSizedOrCatalystIngredient::ingredient,
		ByteBufCodecs.FLOAT,
		FloatSizedOrCatalystIngredient::count,
		FloatSizedOrCatalystIngredient::new);

	/**
	 * Helper method to create a simple sized ingredient that matches a single item.
	 */
	public static FloatSizedOrCatalystIngredient of(ItemLike item, float count) {
		return new FloatSizedOrCatalystIngredient(Ingredient.of(item), count);
	}

	/**
	 * Helper method to create a simple sized ingredient that matches items in a
	 * tag.
	 */
	public static FloatSizedOrCatalystIngredient of(TagKey<Item> tag, int count) {
		return new FloatSizedOrCatalystIngredient(Ingredient.of(tag), count);
	}

	private final Ingredient ingredient;
	private final float count;
	@Nullable
	private FloatemStack[] cachedStacks;

	public FloatSizedOrCatalystIngredient(Ingredient ingredient, float count) {
		this.ingredient = ingredient;
		this.count = count;
	}

	public Ingredient ingredient() {
		return ingredient;
	}

	public float count() {
		return count;
	}

	/**
	 * Performs a size-sensitive test on the given stack.
	 *
	 * @return {@code true} if the stack matches the ingredient and has at least the
	 *         required count.
	 */
	public boolean test(FloatemStack stack) {
		return ingredient.test(stack.getStack()) && stack.getCount() >= count;
	}
	public boolean testWithPart(FloatemStack stack,int parts) {
		return ingredient.test(stack.getStack()) && stack.getCount() >= count*parts;
	}
	/**
	 * Returns a list of the stacks from this {@link #ingredient}, with an updated
	 * {@link #count}.
	 *
	 * @implNote the array is cached and should not be modified, just like
	 *           {@link Ingredient#getItems()}.
	 */
	public FloatemStack[] getItems() {
		if (cachedStacks == null) {
			cachedStacks = Stream.of(ingredient.getItems())
				.map(s -> new FloatemStack(s, count))
				.toArray(FloatemStack[]::new);
		}
		return cachedStacks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FloatSizedOrCatalystIngredient other)) return false;
		return count == other.count && ingredient.equals(other.ingredient);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredient, count);
	}

	@Override
	public String toString() {
		return count + "x " + ingredient;
	}

	public boolean test(ItemStack is) {
		return ingredient.test(is) && is.getCount() >= count;
	}
}

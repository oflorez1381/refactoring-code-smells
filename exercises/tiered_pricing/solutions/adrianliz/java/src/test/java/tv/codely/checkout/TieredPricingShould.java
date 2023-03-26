package tv.codely.checkout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import tv.codely.checkout.mother.IntegerMother;
import tv.codely.checkout.mother.SubscriptionTierMother;
import tv.codely.checkout.mother.SubscriptionTierPriceMother;
import tv.codely.checkout.mother.SubscriptionTierRangeMother;

public class TieredPricingShould {

    private static Set<SubscriptionTier> defaultSubscriptionTiers() {
        final var firstTierRange = SubscriptionTierRange.first(2);
        final var secondTierRange = SubscriptionTierRange.from(firstTierRange, 7);
        final var thirdTierRange = SubscriptionTierRange.from(secondTierRange, 14);
        final var fourthTierRange = SubscriptionTierRange.from(thirdTierRange, 24);
        final var lastTierRange = SubscriptionTierRange.last(fourthTierRange);
        return Set.of(
            new SubscriptionTier(
                firstTierRange,
                new SubscriptionTierPrice(299)),
            new SubscriptionTier(
                secondTierRange,
                new SubscriptionTierPrice(239)),
            new SubscriptionTier(
                thirdTierRange,
                new SubscriptionTierPrice(219)),
            new SubscriptionTier(
                fourthTierRange,
                new SubscriptionTierPrice(199)),
            new SubscriptionTier(
                lastTierRange,
                new SubscriptionTierPrice(149)));
    }

    @Test
    void unit_test_example() {
        assertTrue(true);
    }

    @Test
    void return_total_price_based_on_number_of_subscriptions() {
        final var subscriptionTiers = SubscriptionTierMother.randoms();
        final var tieredPricing = new TieredPricing(subscriptionTiers);
        final var numberOfSubscriptions = IntegerMother.random();
        final var expectedPrice =
            tieredPricing.getBasePrice(numberOfSubscriptions) * numberOfSubscriptions;

        final var totalPrice = tieredPricing.getTotalPrice(numberOfSubscriptions);

        assertEquals(expectedPrice, totalPrice);
    }

    @Test
    void return_total_price_for_1_subscription() {
        final var tieredPricing = new TieredPricing(defaultSubscriptionTiers());
        final var expectedPrice = 299;

        final var totalPrice = tieredPricing.getTotalPrice(1);
        assertEquals(expectedPrice, totalPrice);
    }

    @Test
    void return_total_price_for_3_subscriptions() {
        final var tieredPricing = new TieredPricing(defaultSubscriptionTiers());
        final var expectedPrice = 239 * 3;

        final var totalPrice = tieredPricing.getTotalPrice(3);
        assertEquals(expectedPrice, totalPrice);
    }

    @Test
    void return_total_price_for_11_subscriptions() {
        final var tieredPricing = new TieredPricing(defaultSubscriptionTiers());
        final var expectedPrice = 219 * 11;

        final var totalPrice = tieredPricing.getTotalPrice(11);
        assertEquals(expectedPrice, totalPrice);
    }

    @Test
    void return_total_price_for_26_subscriptions() {
        final var tieredPricing = new TieredPricing(defaultSubscriptionTiers());
        final var expectedPrice = 199 * 26;

        final var totalPrice = tieredPricing.getTotalPrice(26);
        assertEquals(expectedPrice, totalPrice);
    }

    @Test
    void return_total_price_for_51_subscriptions() {
        final var tieredPricing = new TieredPricing(defaultSubscriptionTiers());
        final var expectedPrice = 149 * 51;

        final var totalPrice = tieredPricing.getTotalPrice(51);
        assertEquals(expectedPrice, totalPrice);
    }

    @Test
    void throw_invalid_subscription_tiers_if_there_is_no_tiers() {
        assertThrows(InvalidSubscriptionTiers.class, () -> new TieredPricing(Set.of()));
    }

    @Test
    void throw_invalid_subscription_tiers_if_there_is_no_last_tier() {
        final var subscriptionTiers =
            Set.of(SubscriptionTierMother.create(SubscriptionTierRangeMother.first(10),
                SubscriptionTierPriceMother.random()));

        assertThrows(InvalidSubscriptionTiers.class, () -> new TieredPricing(subscriptionTiers));
    }

    @Test
    void throw_invalid_subscription_tier_range_if_number_of_susbcriptions_is_less_than_1() {
        assertThrows(InvalidSubscriptionTierRange.class, () -> SubscriptionTierRange.first(0));
    }

    @Test
    void throw_invalid_subscription_tier_range_if_previous_range_does_not_exist() {
        assertThrows(InvalidSubscriptionTierRange.class, () -> SubscriptionTierRange.last(null));
    }

    @Test
    void throw_price_not_found_if_number_of_subscriptions_is_not_valid() {
        final var tieredPricing = new TieredPricing(defaultSubscriptionTiers());
        assertThrows(PriceNotFound.class, () -> tieredPricing.getTotalPrice(0));
    }
}

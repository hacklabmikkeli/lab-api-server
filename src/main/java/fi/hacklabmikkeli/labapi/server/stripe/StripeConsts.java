package fi.hacklabmikkeli.labapi.server.stripe;

/**
 * Class for stripe constants
 * 
 * @author Heikki Kurhinen
 */
public class StripeConsts {

  public static final String STRIPE_API_KEY_SETTING = "stripe-api-key";
  
  public static final String STRIPE_SIGNING_KEY_SETTING = "stripe-signing-key";

  public static final String STRIPE_LIVE_MODE_SETTING = "stripe-live-mode";

  public static final String STRIPE_WEBHOOK_TYPE = "stripe";

  public static final String STRIPE_SIGNATURE_HEADER = "Stripe-Signature";
}
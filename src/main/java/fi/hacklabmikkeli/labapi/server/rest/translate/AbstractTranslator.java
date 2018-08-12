package fi.hacklabmikkeli.labapi.server.rest.translate;

import org.apache.commons.lang3.EnumUtils;

/**
 * Abstract base class for all translators
 *
 * @author Heikki Kurhinen 
 */
public abstract class AbstractTranslator {

    /**
     * Translates an enum into another enum with same values
     * 
     * @param targetClass target enum class
     * @param original original enum
     * @return translated enum
     */
    protected <E extends Enum<E>> E translateEnum(Class<E> targetClass, Enum<?> original) {
      if (original == null) {
        return null;
      }
      
      return EnumUtils.getEnum(targetClass, original.name());
  }
}
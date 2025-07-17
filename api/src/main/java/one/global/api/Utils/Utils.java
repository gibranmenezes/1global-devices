package one.global.api.Utils;

import one.global.api.domain.enums.State;


public interface Utils {

    static boolean isProvided(String param) {
        return param != null && !param.isBlank();
    }

    static String getNameFromState(State state) {
        return state != null ? state.name() : null;
    }

   static boolean isUpdatingNameAndBrand(String name, String brand) {
       return isProvided(name) || isProvided(brand);
   }

    static State getValidState(String state) {
        return state != null && !state.isBlank() ? State.valueOf(state.toUpperCase()) : null;
    }
}

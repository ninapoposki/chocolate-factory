package validations;

import beans.Chocolate;

public class ChocolateValidator {

    public static boolean isValidChocolate(Chocolate chocolate) {
        return isValidName(chocolate.getChocolateName()) &&
               isValidPrice(chocolate.getPrice()) &&
               isValidVariety(chocolate.getVariety()) &&
               isValidFactoryId(chocolate.getFactoryId()) &&
               isValidType(chocolate.getType()) &&
               isValidWeight(chocolate.getWeight()) &&
               isValidDescription(chocolate.getDescription());
    }

    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private static boolean isValidPrice(double price) {
        return price > 0;
    }

    private static boolean isValidVariety(String variety) {
        return variety != null && !variety.trim().isEmpty();
    }

    private static boolean isValidFactoryId(int factoryId) {
        return factoryId > 0;
    }

    private static boolean isValidType(String type) {
        return type != null && !type.trim().isEmpty();
    }

    private static boolean isValidWeight(double weight) {
        return weight > 0;
    }

    private static boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty();
    }
}
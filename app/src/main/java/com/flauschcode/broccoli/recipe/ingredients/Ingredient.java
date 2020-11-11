package com.flauschcode.broccoli.recipe.ingredients;

import java.util.Objects;

public class Ingredient {

    // for simplicity reasons this will be a String as long as scaling of recipes is not implemented
    private String quantity;
    private String text;
    private boolean seasonal = false;

    public Ingredient(String quantity, String text) {
        this.quantity = quantity;
        this.text = text;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getText() {
        return text;
    }

    public boolean isSeasonal() {
        return seasonal;
    }

    public void setSeasonal(boolean seasonal) {
        this.seasonal = seasonal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(quantity, that.quantity) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, text);
    }

}

package org.flauschhaus.broccoli.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.flauschhaus.broccoli.ui.recipes.NewRecipeViewModel;
import org.flauschhaus.broccoli.ui.recipes.RecipesViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface ViewModelModule {

    @Binds
    ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(RecipesViewModel.class)
    ViewModel recipesViewModel(RecipesViewModel recipesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NewRecipeViewModel.class)
    ViewModel newRecipeViewModel(NewRecipeViewModel newRecipeViewModel);

}
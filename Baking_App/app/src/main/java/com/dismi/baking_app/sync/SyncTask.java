package com.dismi.baking_app.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import java.net.URL;
import java.util.ArrayList;

import com.dismi.baking_app.database.RecipesProvider;
import com.dismi.baking_app.utils.BakingJsonUtils;
import com.dismi.baking_app.utils.NetworkUtils;


class SyncTask {


    synchronized static void syncRecipeList(Context context) {
        try {
            URL recipeListUrl = NetworkUtils.getBakingUrl();

            String jsonRecipeListResponse = NetworkUtils.getResponseFromHttpsUrl(recipeListUrl);

            ArrayList<ContentValues[]> jsonData = BakingJsonUtils
                    .getResultList(jsonRecipeListResponse);

            if (jsonData != null && jsonData.size() != 0) {

                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(
                        RecipesProvider.Recipes.CONTENT_URI,
                        null,
                        null
                );

                contentResolver.delete(
                        RecipesProvider.Ingredients.CONTENT_URI,
                        null,
                        null
                );

                contentResolver.delete(
                        RecipesProvider.Steps.CONTENT_URI,
                        null,
                        null
                );

                for (int i = 0; i < jsonData.size(); i++) {
                    switch (i) {
                        case BakingJsonUtils.INDEX_RECIPES:
                            contentResolver.bulkInsert(
                                    RecipesProvider.Recipes.CONTENT_URI,
                                    jsonData.get(i)
                            );
                            break;

                        case BakingJsonUtils.INDEX_INGREDIENTS:
                            contentResolver.bulkInsert(
                                    RecipesProvider.Ingredients.CONTENT_URI,
                                    jsonData.get(i)
                            );
                            break;

                        case BakingJsonUtils.INDEX_STEPS:
                            contentResolver.bulkInsert(
                                    RecipesProvider.Steps.CONTENT_URI,
                                    jsonData.get(i)
                            );
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
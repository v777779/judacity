package ru.vpcb.btplay.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ru.vpcb.btplay.FragmentDetail;
import ru.vpcb.btplay.FragmentDetailItem;
import ru.vpcb.btplay.RecipeItem;

import static ru.vpcb.btplay.utils.Constants.CHILD_TYPE;
import static ru.vpcb.btplay.utils.Constants.COLLAPSED_TYPE;
import static ru.vpcb.btplay.utils.Constants.EXPANDED_TYPE;

/**
 * Created by V1 on 13-Nov-17.
 */

public class FragmentData {
    private static String[] cards = new String[]{
            "Recipe Green   Card 1",
            "Recipe Blue    Card 2",
            "Recipe Red     Card 3",
            "Recipe Yellow  Card 4",
            "Recipe Brown   Card 5",
            "Recipe Magenta Card 6",
            "Recipe White   Card 7",
            "Recipe Orange  Card 8"
    };

    private static String[] details = new String[]{
            "Recipe Ingredients",
            "Details Purple Step 1",
            "Details Orange Step 2",
            "Details White  Step 3",
            "Details Red    Step 4",
            "Details Yellow Step 5",
            "Details Green  Step 6",
            "Details Black  Step 7",
            "Details Blue   Step 8"
    };
    private static String[] childs = new String[]{
            "Ingredients:"
    };

    public static List<String> loadMockCards() {

        return new ArrayList<>(Arrays.asList(cards));
    }

    public static List<FragmentDetailItem> loadMockDetails() {
        List<FragmentDetailItem> list = new ArrayList<>();
        for (int i = 0; i < details.length; i++) {
            if (i == 0) {
                FragmentDetailItem childItem = new FragmentDetailItem(childs[i], CHILD_TYPE, null);
                list.add(new FragmentDetailItem(details[i], EXPANDED_TYPE, childItem));
            } else {
                list.add(new FragmentDetailItem(details[i]));
            }
        }
        return list;
    }

    public static void  addImages(List<RecipeItem> list) {
        //test!!!
        String[] links = new String[]{
                "http://i.ndtvimg.com/i/2015-08/10-best-baking-recipes-2_625x350_81438697411.jpg",
                "http://www.fnstatic.co.uk/images/content/recipe/forest-fruit-cake.jpg",
                "http://www.bakewithstork.com/assets/Recipes/_resampled/croppedimage733456-Birthday-Cake-with-Cream-and-Fresh-Fruit.jpg",
                "http://www.countrycrock.com/Images/347/347-1131912-Baked_Goods_recipe_Landing_Hero.png"
        };
        String[] linkThumbs = new String[]{
                "http://www.rudyanddelilah.com/wp-content/uploads/2017/06/Grandma-Rudys-Frozen-Grape-Salad-Thumbnail-600x570.jpg",
                "https://www.biggerbolderbaking.com/wp-content/uploads/bb-plugin/cache/BBB84-Chocolate-Cake-Thumbnail-FINAL-landscape.jpeg",
                "http://cdn-image.myrecipes.com/sites/default/files/image/recipes/ck/04/10/orange-cake-ck-701058-x.jpg",
                "http://img.taste.com.au/GdH23_iI/w720-h480-cfill-q80/taste/2016/11/orange-almond-sour-cream-cake-2332-1.jpeg",
                "https://renditions-tastemade.akamaized.net/61426465-hasselback-baked-apple-lc/thumbnail-1920x1080-00001.png",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZK7FntIsI7rJ_lHW3Gwa6CdKgteg7wpzSP4xtPCaIGxuuY0GX",
                "https://www.biggerbolderbaking.com/wp-content/uploads/2015/05/BBB66-Brownie-Layer-Cake-Thumbnail-v.1-1024x576.jpg",
                "https://www.biggerbolderbaking.com/wp-content/uploads/2014/09/BBB32-Homemade-Donuts-Thumbnail-newest-1024x576.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/e/ec/Cinnamon_rolls%2C_ready_for_cutting_and_baking.jpg",
                "http://cdn.playbuzz.com/cdn/065503d0-c66b-41f7-8b5b-9dad0cfd018a/20330f15-4343-42dc-baea-bcb702896f49.jpg",
                "https://realfood.tesco.com/media/images/BramelyAppleTart_HERO-a3bdb18a-e8dc-46c4-84e8-f7505faf7fb2-0-472x310.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1irZ8PRAUBDJy7eRSq3zNmWizICUCSP3vr5aiqP7P_SIySrfk",
                "https://i0.wp.com/bakingamoment.com/wp-content/uploads/2016/09/9901featured2.jpg?resize=720%2C720&ssl=1",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOQvBvITmMUX4enzf1ENsHa12umUnYnAl04z2-CjFhzWAdXr0UNQ",
                "http://www.waitrose.com/content/dam/waitrose/recipes/images/m/WRWK081216_Martha-Collison_Pudding_Macarons.jpg/_jcr_content/renditions/cq5dam.thumbnail.400.400.png",
                "http://3o45wf6y35-flywheel.netdna-ssl.com/wp-content/uploads/2014/08/pumpkin-pie-mini-tarts-thumbnail-1024x687.jpg",
        };
        Random rnd = new Random();
        for (RecipeItem recipeItem : list) {
            if (recipeItem.getImage().isEmpty()) {
                recipeItem.setImage(links[rnd.nextInt(links.length)]);
            }
            List<RecipeItem.Step> steps = recipeItem.getSteps();
            for (RecipeItem.Step step : steps) {
                if (step.getThumbnailURL().isEmpty()) {
                    step.setThumbnailURL(linkThumbs[rnd.nextInt(linkThumbs.length)]);
                }
            }
        }
// test!!!

    }
}

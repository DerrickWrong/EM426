package em426.fx;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.paint.*;

import java.util.*;

/** a class to generate a color set and map the colors with a limited set of UUIDs
 *
 */
public class ColorFactory {

    private MapProperty<UUID, String> colors;

    //TODO allow a pattern or library of various color sets, for different use
    //TODO - allow external setting of color set
    private static final String[] SeriesColors = {
            "#e41a1c","#377eb8","#4daf4a","#984ea3","#ff7f00","#ffff33","#a65628","#f781bf","#999999"
    };

    public ColorFactory(){
        colors = new SimpleMapProperty<UUID, String>(FXCollections.observableHashMap());
    }

    public boolean contains(UUID id){
        return colors.containsKey(id);
    }

    /** returns a string representing a color from the palette for this id.
     * If not yet present, adds and allocates a color
     *
     * @param id - a unique ID for an object to associate with the color
     * @return a string represneting a color useful in web color schemes, such as #ffffff for white or WHITE
     */
    public String get(UUID id){
        put(id);
        return colors.get(id);
    }

    /** returns a color from the palette for this id. If not yet present, adds and allocates a color
     * @param id
     * @return
     */
    public Color getColor(UUID id){
        return Color.valueOf(get(id));
    }

    /**
     * If the color map does not include this id, adds it with an allocated color from the palette
     * @param id
     */
    public void put(UUID id){
        if (contains(id))
            return;
        var dColor = getNextColor();
        colors.put(id, dColor);
    }

    public void remove(UUID id){
        colors.remove(id);
    }

    /**
     * @return A map of colors to specific unique IDs.
     * If the UUID exists in the color map, the figures shows the color in the row for that element.
     */
    public MapProperty<UUID, String> colorsProperty(){
        return colors;
    }
    /**
     * Looks for the least utilized color and returns for next use
     * @return a color from the color series
     */
    private String getNextColor(){
        // find the color with the least frequency and use it for this new Series.
        int minColor = 0;
        int minCount = Integer.MAX_VALUE;
        for (int m = 0; m< SeriesColors.length; m++) {
            int count = Collections.frequency(colors.values(), SeriesColors[m]);
            if (count < minCount) {
                minColor = m;
                minCount = count;
            }
        }
        return  SeriesColors[minColor];
    }
}

package in.prajwal.radius.model;

/**
 * Created by prajwalgs on 18/08/18.
 */

public class FacilityOption {
    private String optionName, optionIcon;
    private int id;

    public FacilityOption(int id, String optionName, String optionIcon){
        this.optionName = optionName;
        this.optionIcon = optionIcon;
        this.id = id;
    }

    public int getId(){return id;}

    public String getOptionName(){return optionName;}

    public String getOptionIcon(){return optionIcon;}
}

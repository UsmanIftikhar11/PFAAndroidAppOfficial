package com.pfa.pfaapp.utils;

import com.pfa.pfaapp.localdbmodels.DistrictInfo;
import com.pfa.pfaapp.localdbmodels.DivisionInfo;
import com.pfa.pfaapp.localdbmodels.RegionInfo;
import com.pfa.pfaapp.localdbmodels.SubTownInfo;
import com.pfa.pfaapp.localdbmodels.TownInfo;

import java.util.ArrayList;
import java.util.List;

public class DropdownNameListUtils {
    private boolean isEnglish;

    public DropdownNameListUtils(boolean isEnglish) {
        this.isEnglish = isEnglish;
    }

    public List<String> getRegionNames(List<RegionInfo> regionInfos) {
        List<String> districtNames = new ArrayList<>();
        if (regionInfos.size() > 0) {
            for (RegionInfo districtInfo : regionInfos)
                districtNames.add(districtInfo.getRegion_name());
        }
        return districtNames;
    }

    public List<String> getDivisionNames(List<DivisionInfo> divisionInfos) {
        List<String> districtNames = new ArrayList<>();
        if (divisionInfos.size() > 0) {
            for (DivisionInfo districtInfo : divisionInfos) {
                if (isEnglish)
                    districtNames.add(districtInfo.getDivision_name());
                else
                    districtNames.add(districtInfo.getDivision_nameUrdu());
            }
        }
        return districtNames;
    }

    public List<String> getDistrictNames(List<DistrictInfo> districtInfos) {
        List<String> districtNames = new ArrayList<>();
        if (districtInfos.size() > 0) {
            for (DistrictInfo districtInfo : districtInfos) {
                if (isEnglish)
                    districtNames.add(districtInfo.getDistrict_name());
                else districtNames.add(districtInfo.getDistrict_nameUrdu());
            }
        }
        return districtNames;
    }

    public List<String> getTownNames(List<TownInfo> townInfos) {
        List<String> districtNames = new ArrayList<>();
        if (townInfos.size() > 0) {
            for (TownInfo districtInfo : townInfos) {
                if (isEnglish)
                    districtNames.add(districtInfo.getTown_name());
                else
                    districtNames.add(districtInfo.getTown_nameUrdu());
            }
        }
        return districtNames;
    }


    public List<String> getSubTownNames(List<SubTownInfo> subTownInfos) {
        List<String> districtNames = new ArrayList<>();
        if (subTownInfos.size() > 0) {
            for (SubTownInfo districtInfo : subTownInfos) {
                if (isEnglish)
                    districtNames.add(districtInfo.getSubtown_name());
                else
                    districtNames.add(districtInfo.getSubtown_nameUrdu());
            }
        }
        return districtNames;
    }


}

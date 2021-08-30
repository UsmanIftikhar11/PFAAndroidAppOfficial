package com.pfa.pfaapp.customviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.pfa.pfaapp.DropdownActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.PFATextWatcher;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.localdbmodels.DistrictInfo;
import com.pfa.pfaapp.localdbmodels.DivisionInfo;
import com.pfa.pfaapp.localdbmodels.RegionInfo;
import com.pfa.pfaapp.localdbmodels.SubTownInfo;
import com.pfa.pfaapp.localdbmodels.TownInfo;
import com.pfa.pfaapp.models.AddressObjInfo;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.DropdownNameListUtils;

import java.util.ArrayList;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTV_TAG;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DROPDOWN_NAME;
import static com.pfa.pfaapp.utils.AppConst.RC_DROPDOWN;
import static com.pfa.pfaapp.utils.AppConst.SEARCH_DATA;

public class PFALocationACTV extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private List<String> listItemNames;
    DropdownNameListUtils dropdownNameListUtils;
    private int selectedID = -1;
    AppUtils appUtils;
    private Context mContext;
    private List<FormDataInfo> selectedValues = new ArrayList<>();
    String dropdownName;
    WhichItemClicked whichItemClicked;
    FormDataInfo filterDataInfo;

    List<RegionInfo> regionInfos;
    List<DivisionInfo> divisionInfos;
    List<DistrictInfo> districtInfos;
    List<TownInfo> townInfos;
    List<SubTownInfo> subTownInfos;

    AddressObjInfo default_locations;
    private PFATextInputLayout textInputLayout;

//    @SuppressLint("RtlHardcoded")
    public PFALocationACTV(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //
        setKeyListener(null);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        appUtils = new AppUtils(context);
        dropdownNameListUtils = new DropdownNameListUtils(appUtils.isEnglishLang());

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        setLayoutParams(params);

        addTextChangedListener(new PFATextWatcher(new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (!getText().toString().isEmpty()) {
                    final ViewParent parent = getParent();
                    if (parent instanceof TextInputLayout) {
                        ((TextInputLayout) parent).setHint(getHint().toString());
                    }
                }
            }
        }));

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (dropdownName == null)
                    return;
                if (hasFocus) {
                    setHint("");
                    performClick();
                } else {
                    setHint(dropdownName);
                }
            }
        });
    }

    public void setItemClickCallback(WhichItemClicked whichItemClicked, FormDataInfo filterDataInfo, FormFieldInfo fieldInfo) {
        this.whichItemClicked = whichItemClicked;
        this.filterDataInfo = filterDataInfo;

        if (selectedValues != null && selectedValues.size() > 0)
            selectedValues.clear();

        if (fieldInfo != null && fieldInfo.getDefault_locations() != null) {
            this.default_locations = fieldInfo.getDefault_locations();
        }
        setClicListner();

    }

    public void setFormFieldInfo(FormFieldInfo fieldInfo) {
        this.default_locations = fieldInfo.getDefault_locations();
        whichItemClicked = null;
    }

    public PFALocationACTV(Context context) {
        super(context);
    }

    private FormDataInfo createFormDataInfo(int key, String value) {
        FormDataInfo filterDataInfo = new FormDataInfo();
        filterDataInfo.setKey("" + key);
        filterDataInfo.setValue("" + value);
        filterDataInfo.setName(getTag().toString());
        filterDataInfo.setSelected(true);

        return filterDataInfo;
    }

    public void setRegionData(final List<RegionInfo> regionInfos) {

        this.regionInfos = regionInfos;

        dropdownName = mContext.getString(R.string.region);
        listItemNames = dropdownNameListUtils.getRegionNames(regionInfos);
        if (filterDataInfo == null && default_locations != null) {
            filterDataInfo = createFormDataInfo(default_locations.getRegion_id(), default_locations.getRegion_name());
            selectedValues.add(filterDataInfo);
        }

        setSelectedPosition(filterDataInfo);
    }

    public void setRegionDropdownSelection(int position) {
        if (position == -1)
            return;
        selectedValues.clear();

        FormDataInfo formDataInfo = createFormDataInfo(regionInfos.get(position).getRegion_id(), listItemNames.get(position));
        selectedValues.add(formDataInfo);

        setSelectedPosition(formDataInfo);

        setSelectedID(regionInfos.get(position).getRegion_id());

        if (whichItemClicked != null)
            whichItemClicked.whichItemClicked("" + (regionInfos.get(position).getRegion_id()));
    }

    public void setDivisionData(List<DivisionInfo> divisionInfos) {
        dropdownName = mContext.getString(R.string.division);
        this.divisionInfos = divisionInfos;
        listItemNames = dropdownNameListUtils.getDivisionNames(divisionInfos);

        if (filterDataInfo == null && default_locations != null) {
            filterDataInfo = createFormDataInfo(default_locations.getDivision_id(), default_locations.getDivision_name());
            selectedValues.add(filterDataInfo);
        }
        setSelectedPosition(filterDataInfo);
    }

    public void setDivisionDropdownSelection(int position) {
        if (position == -1)
            return;
        selectedValues.clear();

        FormDataInfo formDataInfo = createFormDataInfo(divisionInfos.get(position).getDivision_id(), listItemNames.get(position));
        selectedValues.add(formDataInfo);
        setSelectedPosition(formDataInfo);

        setSelectedID(divisionInfos.get(position).getDivision_id());

        if (whichItemClicked != null)
            whichItemClicked.whichItemClicked("" + (divisionInfos.get(position).getDivision_id()));
    }

    public void setDistrictsData(final List<DistrictInfo> districtInfos) {
        this.districtInfos = districtInfos;
        dropdownName = mContext.getString(R.string.district);
        listItemNames = dropdownNameListUtils.getDistrictNames(districtInfos);

        if (filterDataInfo == null && default_locations != null) {
            filterDataInfo = createFormDataInfo(default_locations.getDistrict_id(), default_locations.getDistrict_name());
            selectedValues.add(filterDataInfo);
        }

        setSelectedPosition(filterDataInfo);
    }

    public void setDistrictDropdownSelection(int position) {
        if (position == -1)
            return;
        selectedValues.clear();
        FormDataInfo formDataInfo = createFormDataInfo(districtInfos.get(position).getDistrict_id(), listItemNames.get(position));

        selectedValues.add(formDataInfo);
        setSelectedPosition(formDataInfo);

        setSelectedID(districtInfos.get(position).getDistrict_id());

        if (whichItemClicked != null)
            whichItemClicked.whichItemClicked("" + (districtInfos.get(position).getDistrict_id()));
    }

    public void setTowns(final List<TownInfo> townInfos) {
        this.townInfos = townInfos;
        dropdownName = mContext.getString(R.string.town);
        listItemNames = dropdownNameListUtils.getTownNames(townInfos);

        if (filterDataInfo == null && default_locations != null) {
            filterDataInfo = createFormDataInfo(default_locations.getTown_id(), default_locations.getTown_name());
            selectedValues.add(filterDataInfo);
        }
        setSelectedPosition(filterDataInfo);
    }

    public void setTownsDropdownSelection(int position) {
        if (position == -1)
            return;
        selectedValues.clear();
        FormDataInfo formDataInfo = createFormDataInfo(townInfos.get(position).getTown_id(), listItemNames.get(position));
        selectedValues.add(formDataInfo);
        setSelectedPosition(formDataInfo);

        setSelectedID(townInfos.get(position).getTown_id());

        if (whichItemClicked != null)
            whichItemClicked.whichItemClicked("" + (townInfos.get(position).getTown_id()));
    }

    public void setSubTowns(final List<SubTownInfo> subTownInfos) {
        this.subTownInfos = subTownInfos;
        dropdownName = mContext.getString(R.string.subtown);
        listItemNames = dropdownNameListUtils.getSubTownNames(subTownInfos);

        if (filterDataInfo == null && default_locations != null) {
            filterDataInfo = createFormDataInfo(default_locations.getSubtown_id(), default_locations.getSubtown_name());
            selectedValues.add(filterDataInfo);
        }

        setSelectedPosition(filterDataInfo);
    }

    public void setSubTownsDropdownSelection(int position) {
        if (position == -1)
            return;
        selectedValues.clear();

        FormDataInfo formDataInfo = createFormDataInfo(subTownInfos.get(position).getSubtown_id(), listItemNames.get(position));

        selectedValues.add(formDataInfo);
        setSelectedPosition(formDataInfo);

        setSelectedID(subTownInfos.get(position).getSubtown_id());

        if (whichItemClicked != null)
            whichItemClicked.whichItemClicked("" + (subTownInfos.get(position).getSubtown_id()));

    }

    private void setSelectedPosition(FormDataInfo filterDataInfo) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(false);
        } else {
            setFocusable(false);
        }

        if (filterDataInfo != null)
            for (int i = 0; i < listItemNames.size(); i++) {

                if (filterDataInfo.getValue().equalsIgnoreCase(listItemNames.get(i)) || filterDataInfo.getValueUrdu().equalsIgnoreCase(listItemNames.get(i))) {
                    setText(listItemNames.get(i));
                    textInputLayout.setError(null);

                    if (whichItemClicked != null)
                        whichItemClicked.whichItemClicked(filterDataInfo.getKey());
                }
            }
    }

    private void setClicListner() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startDropDownActivity();
            }
        });
    }

    public int getSelectedID() {
        return selectedID;
    }

    public void setSelectedID(int selectedID) {
        this.selectedID = selectedID;
    }

    public List<FormDataInfo> getSelectedValues() {
        return selectedValues;
    }


    public void startDropDownActivity() {
        Intent intent = new Intent(mContext, DropdownActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(EXTRA_DROPDOWN_NAME, appUtils.isEnglishLang()?("Select " + dropdownName):
                (dropdownName+" منتخب کریں "));
        bundle.putStringArrayList(SEARCH_DATA, (ArrayList<String>) listItemNames);
        bundle.putString(EXTRA_ACTV_TAG, getTag().toString());
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent, RC_DROPDOWN);
    }

    public PFATextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public void setTextInputLayout(PFATextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public void setSearchRegionID(int searchRegionID) {
        if (regionInfos != null && regionInfos.size() > 0) {
            for (int i = 0; i < regionInfos.size(); i++) {
                if (regionInfos.get(i).getRegion_id() == searchRegionID) {
                    setRegionDropdownSelection(i);
                    break;
                }
            }
        }
    }


    public void setSearchDivID(int searchDivID) {
        if (divisionInfos != null && divisionInfos.size() > 0) {
            for (int i = 0; i < divisionInfos.size(); i++) {
                if (divisionInfos.get(i).getDivision_id() == searchDivID) {
                    setDivisionDropdownSelection(i);
                    break;
                }
            }
        }
    }

    public void setSearchDistID(int searchDistID) {
        if (districtInfos != null && districtInfos.size() > 0) {
            for (int i = 0; i < districtInfos.size(); i++) {
                if (districtInfos.get(i).getDistrict_id() == searchDistID) {
                    setDistrictDropdownSelection(i);
                    break;
                }
            }
        }
    }

    public void setSearchTownID(int searchTownID) {
        if (townInfos != null && townInfos.size() > 0) {
            for (int i = 0; i < townInfos.size(); i++) {
                if (townInfos.get(i).getTown_id() == searchTownID) {
                    setTownsDropdownSelection(i);
                    break;
                }
            }
        }
    }

    public void setSearchSubTownID(int searchSubTownID) {
        if (subTownInfos != null && subTownInfos.size() > 0) {
            for (int i = 0; i < subTownInfos.size(); i++) {
                if (subTownInfos.get(i).getSubtown_id() == searchSubTownID) {
                    setSubTownsDropdownSelection(i);
                    break;
                }
            }
        }
    }
}

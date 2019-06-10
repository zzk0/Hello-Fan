package com.test.fan.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/*
*保存从数据库中的表dict取出的条目
 */
public class DictBean implements SearchSuggestion {

    private String words;

    private String spell;

    private String express;

    public DictBean(String words, String spell, String express) {
        this.words = words;
        this.spell = spell;
        this.express = express;
    }

    public DictBean() {
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }


    /*
    *为了适配第三方控件弄的，没啥用
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(words);
        dest.writeString(spell);
        dest.writeString(express);
    }
    public static final Parcelable.Creator<DictBean> CREATOR = new Creator<DictBean>() {

        @Override
        public DictBean[] newArray(int size) {
            return new DictBean[size];
        }

        @Override
        public DictBean createFromParcel(Parcel source) {
            return new DictBean(source.readString(), source.readString(),
                    source.readString());
        }
    };

    @Override
    public String getBody() {
        return words;
    }
}


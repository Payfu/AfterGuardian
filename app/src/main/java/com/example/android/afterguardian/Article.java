package com.example.android.afterguardian;

public class Article {

    private String mPillarName, mUrl, mTitleArticle, mPublicationDate, mAuthor;

    /**
     *
     * @param pillarName is name of the category
     * @param titleArticle is the title of the article
     * @param publicationDate is the publication date
     * @param url is the url of the article
     * @param author is the name of the author
     */
    public Article(String pillarName, String titleArticle, String publicationDate, String url, String author){
        mPillarName = pillarName;
        mTitleArticle = titleArticle;
        mPublicationDate = publicationDate;
        mUrl = url;
        mAuthor = author;

    }

    public String getPillarName()       { return mPillarName.replaceAll(" ", "_").toLowerCase(); }
    public String getTitleArticle()     { return mTitleArticle; }
    public String getPublicationDate()  { return mPublicationDate; }
    public String getUrl()              { return mUrl; }
    public String getAuthor()           { return mAuthor; }
}
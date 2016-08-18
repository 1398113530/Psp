package org.ppsspp.ppsspp;

/**
 * Created by jalen on 2016/8/17.
 */
public class ResponseResult {

    /**
     * id : 33
     * type : psp
     * xiaoji_game_id : 9001087
     * ch_name : 国际网球公开赛3（美版）
     * en_name : Smash Court Tennis 3
     * language : 英文
     * category : 体育游戏
     * icon : http://img.vgabc.com/files/9001087/0517b971c236b14430211ee797820afd.png
     * preview : ["http://img.vgabc.com/files/9001087/54284bf1846fec05faaf1a83fd07472c.jpg!300x225","http://img.vgabc.com/files/9001087/cb220e955fe2acff58d60609c380d545.jpg!300x225","http://img.vgabc.com/files/9001087/c3939b4b3c669d3810c448dbd61619f8.jpg!300x225","http://img.vgabc.com/files/9001087/380dbed9b8bdbf39379b8c79f63c2f68.jpg!300x225","http://img.vgabc.com/files/9001087/e0768f548076cac7242829dd1814af6c.jpg!300x225"]
     * download_url : http://test-gd2.xiaoji001.com/rom/psp/9001087.zip
     * intro : 在《国际网球公开赛3》中，共收录包括现世界排名第一，瑞士名将费徳勒、西班牙的纳达尔，以及比利时女选手海宁等在内的15名职业选手登场，让玩家可以跟这些世界著名选手一较长短。此外，在这次游戏中还收录好几款迷你小游戏让玩家能在刺激的网球竞技之余也可以轻松一下，而且本作还支持Ad-Hoc无线多人联机对战，让玩家也可以跟朋友互相来打一场轻松的友谊赛。
     * create_time : 1470988698
     * status : 1
     */

    private String id;
    private String type;
    private String xiaoji_game_id;
    private String ch_name;
    private String en_name;
    private String language;
    private String category;
    private String icon;
    private String preview;
    private String download_url;
    private String intro;
    private String create_time;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXiaoji_game_id() {
        return xiaoji_game_id;
    }

    public void setXiaoji_game_id(String xiaoji_game_id) {
        this.xiaoji_game_id = xiaoji_game_id;
    }

    public String getCh_name() {
        return ch_name;
    }

    public void setCh_name(String ch_name) {
        this.ch_name = ch_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

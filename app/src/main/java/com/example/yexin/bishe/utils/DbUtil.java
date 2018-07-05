package com.example.yexin.bishe.utils;

import com.example.yexin.bishe.bean.MediaItemDB;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yexin on 2018/5/23.
 */

public class DbUtil {
    public DbManager dbManager = null;
    private ArrayList<MediaItemDB> mediaItemDBs;

    public DbUtil() {
        if (dbManager == null) {
            DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                    .setDbName("video")
                    .setDbVersion(1)
                    .setDbDir(null)
                    .setTableCreateListener(new DbManager.TableCreateListener() {
                        @Override
                        public void onTableCreated(DbManager db, TableEntity<?> table) {

                        }
                    });
            dbManager = x.getDb(daoConfig);
        }
    }

    public void add(MediaItemDB itemDB) {
        try {
            dbManager.save(itemDB);
            LogUtil.i("success");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void delete(String vid) {
        try {
            WhereBuilder whereBuilder = WhereBuilder.b();
            whereBuilder.and("vid", "=", vid);
            dbManager.delete(MediaItemDB.class, whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MediaItemDB> find() {
        try {
            mediaItemDBs = (ArrayList<MediaItemDB>) dbManager.findAll(MediaItemDB.class);
            for (int i = 0; i < mediaItemDBs.size(); i++) {
                LogUtil.i(mediaItemDBs.get(i).toString());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mediaItemDBs;
    }

    public boolean findById(String id) {
        try {
            MediaItemDB mediaItemDB = dbManager.findById(MediaItemDB.class, id);
            if (mediaItemDB != null) {
                //数据库中有该视频，说明已收藏
                return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
}

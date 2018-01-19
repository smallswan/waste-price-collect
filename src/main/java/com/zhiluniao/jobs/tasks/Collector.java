package com.zhiluniao.jobs.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zhiluniao.jobs.model.entity.CollectTaskEntity;

/**
 * 
 *
 * @author huangshunle<br>
 *         2017年5月15日  下午5:35:51
 */
/**
 * 价格数据采集器
 * 
 * @author huangshunle<br>
 *         2017年5月11日 上午9:29:19
 */
@Slf4j
public class Collector implements Callable<List<CollectTaskEntity>> {
    private static final List<Integer> SUPPORT_CLASS_IDS = new ArrayList<Integer>(Arrays.asList(12, 14, 15, 17, 19, 22,
            33, 39, 40, 61));

    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";

    private int startPage;
    private int endPage;
    private int page;
    private int classId;
    /** 本地保存html页面的根目录 */
    private String savePath;

    Collector() {

    }
    
    Collector(int page, int classId,String savePath) {
    	this.page = page;
        this.classId = classId;
        this.savePath = savePath;
    }

    Collector(int startPage, int endPage, int classId,String savePath) {

        this.startPage = startPage;
        this.endPage = endPage;
        this.classId = classId;
        this.savePath = savePath;
    }

    @Override
    public List<CollectTaskEntity> call() throws Exception {
        log.info("ROOT_PATH : {}", savePath);
        log.info("开始处理分页区间[{},{}]", startPage, endPage);
        String urlFormat = "http://www.zgfp.com/search/searchprice.aspx?page=%d&ChannelId=8&cid=%d&k=title&w=&e=2&d=&a=";
        int page = this.page;
        Document doc;

        List<CollectTaskEntity> tasks = new ArrayList<CollectTaskEntity>();

            if (!SUPPORT_CLASS_IDS.contains(classId)) {
                log.info("目前暂不支持废旧类型[classId=" + classId + "]");
                return null;
            }

            // 解析分页信息
            String requestUrl = String.format(urlFormat, page, classId);
            log.info("requestUrl : {}", requestUrl);
            try {
                Random random = new Random();
                Thread.sleep(random.nextInt(20000));
                doc = Jsoup.connect(requestUrl).timeout(60000).userAgent(userAgent).get();

                // 1. 解析列表页
                Element priceUrlList = doc.select("table#gvArticleList").first();
                Elements priceUrls = priceUrlList.select("tbody > tr");
                for (Element url : priceUrls) {
                    Elements tds = url.select("td");
                    if (tds.size() == 4) {

                        String detailUrl = tds.get(2).select("a").first().attr("href");

                        // 这里假设当天数据当天发布
                        String publishTime = tds.get(3).text();
                        String[] publishDate = publishTime.split(" ")[0].split("/");
                        DateTime date = new DateTime(Integer.valueOf(publishDate[0]), Integer.valueOf(publishDate[1]),
                                Integer.valueOf(publishDate[2]), 0, 0);

                        String path = "http://www.zgfp.com" + detailUrl.substring(2);

                        // 2. 判断页面是否已经下载
                        String allPath = savePath + detailUrl.substring(2);
                        // 先判断本地是否已经下载对应的文件
                        if (isDownload(allPath)) {
                            log.info("文件[{}]已经下载，无需重复下载", allPath);

                            continue;
                        } else {
                        	CollectTaskEntity task = download(path, allPath);
                        	if(task != null){
                        		task.setDate(date.toDate());
                        		tasks.add(task);
                        	}
                        }

                    }
                }
            } catch (HttpStatusException e) {
                log.error("{},{}", e.getStatusCode(), e.getMessage());
                log.error("访问网页出错了", e);
            } catch (IOException e) {
                log.error("", e);
            } catch (Exception e) {
                log.error("", e);
            }
//        }

        return tasks;
    }

    private boolean isDownload(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private CollectTaskEntity download(String remoteUri, String localUri) {
        Document detail;
        try {
            detail = Jsoup.connect(remoteUri).timeout(6000).userAgent(userAgent).get();
            // 保存html页面
            saveHtml(localUri, detail);

            CollectTaskEntity task = new CollectTaskEntity();
            task.setRemoteUri(remoteUri);
            task.setLocalUri(localUri);
            task.setIsDownload("1");
            task.setIsLoad("0");
            task.setCreateTime(new Date());

            return task;
        } catch (Exception e) {
            log.error("下载页面出错了", e);
        }

        return null;

    }

    /**
     * 保存html页面到本地磁盘
     * 
     * @param path
     * @param doc
     */
    private void saveHtml(String path, Document doc) {

        int last = path.lastIndexOf("/");
        String dir = path.substring(0, last + 1);

        FileOutputStream fos;
        OutputStreamWriter osw;
        try {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file, false);
            osw = new OutputStreamWriter(fos, "gb2312");
            osw.write(doc.html());
            osw.close();
        } catch (FileNotFoundException e) {
            log.error("", e);
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }

    }

}

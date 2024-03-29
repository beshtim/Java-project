import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


public class AllNews {

    /**
     * get response
     * @param login user login
     * @return String
     * @throws IOException
     */
        public static String getresp(String login) throws IOException {
        String url = "http://news.jkdev.ru/" + login + "/uinfo";
        String doc = Jsoup.connect(url).get().text();
        String response = doc.toString();
        return response;
    }

    /**
     * get vk subscriptions
     * @param login user login
     * @return ArrayList<String>
     * @throws JSONException
     */
    public static ArrayList<String> vkSubs(String login) throws JSONException, IOException {
        String resp = getresp(login);
        JSONObject obj = new JSONObject(resp);
        JSONArray vk_subs = obj.getJSONObject("subscriptions").getJSONArray("vk");
        ArrayList<String> vkSubsArr = new ArrayList<String>((Collection<? extends String>) vk_subs);
        return vkSubsArr;
    }

    /**
     * get inst subscriptions
     * @param login user login
     * @return ArrayList<String>
     * @throws JSONException
     */
    public static ArrayList<String> instagramSubs(String login) throws JSONException, IOException {
        String resp = getresp(login);
        JSONObject obj = new JSONObject(resp);
        JSONArray instagram_subs = obj.getJSONObject("subscriptions").getJSONArray("istagram");
        ArrayList<String> instagramSubsArr = new ArrayList<String>((Collection<? extends String>) instagram_subs);
        return instagramSubsArr;
    }

    /**
     * get all subscriptions
     * @param login
     * @return ArrayList<String>
     * @throws IOException
     * @throws JSONException
     */
    public static ArrayList<String> getSubs(String login) throws IOException, JSONException {
        ArrayList<String> subs = new ArrayList<>();
        ArrayList<String> vksubs = vkSubs(login);
        try{
            for (String sub: vksubs) {
            subs.add(sub);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        ArrayList<String> intagramsubs = instagramSubs(login);
        try{
            for (String sub: intagramsubs) {
                subs.add(sub);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        for (String sub: intagramsubs) {
            subs.add(sub);
        }
        return subs;
    }

    /**
     * swap two objects
     * @param list list
     * @param i index number one
     * @param j index number two
     */
    public static void swap(ArrayList<Post> list, Integer i, Integer j) {
        Post k = list.get(i);
        list.set(i, list.get(j));
        list.set(j, k);
    }

    /**
     * sort, that sorts first two posts by date
     * @param postObjs array of Post objects
     */
    public static void sortDate(ArrayList<Post> postObjs){
        for (int i = 0; i < 10; i++) {
            for (int j = postObjs.size() - 1; j > i; j--){
                if (postObjs.get(j).date.after(postObjs.get(j - 1).date)) {
                    swap(postObjs, j, j - 1);
                }
            }
        }
    }

    /**
     * sort, that sorts first two posts by date
     * @param postObjs array of Post objects
     */
    public static void sortRate(ArrayList<Post> postObjs){
        for (int i = 0; i < 10; i++) {
            for (int j = postObjs.size() - 1; j > i; j--){
                if ((postObjs.get(j).likes + postObjs.get(j).reposts) >
                        (postObjs.get(j - 1).likes + postObjs.get(j-1).reposts)) {
                    swap(postObjs, j, j - 1);
                }
            }
        }
    }

    /**
     * 10 last news
     * @param login user login
     * @return Post[]
     */
    public static Post[] getLastNews(String login) throws IOException, JSONException {
        ArrayList<String> subs = getSubs(login);
        ArrayList<Post> postObjs = new ArrayList<Post>();
        for (String sub: subs) {
            ArrayList<Post> arr = checkParser(sub);
            for(Post i: arr){
                postObjs.add(i);
            }
        }
        sortDate(postObjs);
        Post[] result = new Post[10];
        for (int j = 0; j < 10; j++) {
            result[j] = postObjs.get(j);
        }
        return result;
    }

    /**
     * 10 best news
     * @param login user login
     * @return Post[]
     */
    public static Post[] getBestNews(String login) throws IOException, JSONException {
        ArrayList<String> subs = getSubs(login);
        ArrayList<Post> postObjs = new ArrayList<Post>();
        for (String sub: subs) {
            ArrayList<Post> arr = checkParser(sub);
            for(Post i: arr){
                postObjs.add(i);
            }
        }
        sortRate(postObjs);
        Post[] result = new Post[10];
        for (int j = 0; j < 10; j++) {
            result[j] = postObjs.get(j);
        }
        return result;
    }

    /**
     * add Subscription
     * @param login username
     * @param socialNetwork type of SN like vk or instagram
     * @param snurl idvk or unstagram's username
     * @return response String
     * @throws IOException
     */
    public static String addSub(String login, String socialNetwork, String snurl) throws IOException {
        String url = String.format("http://news.jkdev.ru/%s/addSubscription?source=%s&subscriptions=%s", login, socialNetwork, snurl);
        String doc = Jsoup.connect(url).get().text();
        String response = doc.toString();
        return response;
    }

    /**
     * delete subscription
     * @param login username
     * @param socialNetwork type of SN like vk or instagram
     * @param snurl url of sourse
     * @return response String
     * @throws IOException
     */
    public static String delSub(String login, String socialNetwork, String snurl) throws IOException {
        String url = String.format("http://news.jkdev.ru/%s/deleteSubscription?source=%s&subscriptions=%s", login, socialNetwork, snurl);
        String doc = Jsoup.connect(url).get().text();
        String response = doc.toString();
        return response;
    }

    /**
     * New user
     * @param login user name
     * @param email email (if he has one)
     * @param telegram telegram acc (if he has one)
     * @return response String
     * @throws IOException
     */
    public static String register(String login, String email, String telegram) throws IOException {
        String pwd = "qwerty";
        String url = String.format("http://news.jkdev.ru/%s/register?pwd=%s&mail=%s&telegram=%s", login, pwd, email, telegram);
        String doc = Jsoup.connect(url).get().text();
        String response = doc.toString();
        return response;
    }

    /**
     *  whitch parser we are using
     * @param sub String subscription
     * @return ArrayList<Post>
     * @throws IOException
     */
    public static ArrayList<Post> checkParser(String sub) throws IOException {
        if (sub.contains("inst")) {
            String url = (String) sub.subSequence(sub.lastIndexOf("/")+1,-1);
            ArrayList<Post> arr = instagram.parse(url);
            return arr;
        } else {
            String url = (String) sub.subSequence(sub.lastIndexOf("/")+1,-1);
            vk.parse(url);
            ArrayList<Post> arr = vk.parse(url);
            return arr;
        }
    }

}

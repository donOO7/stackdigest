package com.stackDigest.stackDigest.webservice;

import com.stackDigest.stackDigest.beans.database.AnswersD;
import com.stackDigest.stackDigest.beans.database.ItemsD;
import com.stackDigest.stackDigest.beans.database.OwnerD;
import com.stackDigest.stackDigest.beans.restfetch.QuestionsAll.Answers;
import com.stackDigest.stackDigest.beans.restfetch.QuestionsAll.Items;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class TestJDBC {
    public static void main(String[] args) {
//        String jdbcUrl="jdbc:mysql://localhost:3306/stackdigest";
//        String user="root";
//        String pass="arpit";
//        try {
////            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection myConn= DriverManager.getConnection(jdbcUrl,user,pass);
//            System.out.println("SUCCESS");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        SessionFactory factory=new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(ItemsD.class)
                .addAnnotatedClass(OwnerD.class)
                .addAnnotatedClass(AnswersD.class)
                .buildSessionFactory();
        Session session=factory.openSession();
        Transaction tx=session.beginTransaction();

        try {
//            System.out.println("Creating items");
//            ItemsD item=new ItemsD();
//            item.setLink("x");
//            item.setAssetId(1);
//            item.setSsdisplayName("a");
//            item.setSsprofileImage("x");
//            item.setBody("bdy");
//            item.setLink("lin");
//            item.setScore(100);
//            item.setTitle("title");
//            Set<String> x=new HashSet<>();
//            x.add("tg");
//            x.add("th");
//            item.setTags(x);
//
//            AnswersD answersD =new AnswersD();
//            answersD.setBody("ab");
//            answersD.setScore(100);
//            answersD.setAssetId(121);
//            item.setAnswersD(answersD);
//            System.out.println(item);


//            Transaction tx=session.beginTransaction();
//            session.save(item);
//            tx.commit();
//            System.out.println("done");

//            final String uri = "https://api.stackexchange.com/2.2/search/advanced?page=1&order=desc&sort=votes&accepted=True&site=stackoverflow&filter=!)EhxQMOPc)dH94o7-NBjAb4AcHxu2_8*7Nua1q2CUHEgIfc*9";

//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//            JsonRootBean result = restTemplate.getForObject(uri, JsonRootBean.class);
//            assert result != null;
//            for (Items x:result.getItems()) {
//                System.out.println(genItemsD(x));
//                session.save(genItemsD(x));
//            }
//
//            tx.commit();
//            System.out.println("done");

            List<ItemsD> items;
            Session session1=factory.getCurrentSession();
            Query<ItemsD> query=session.createQuery("from ItemsD");
            items=query.list();
            System.out.println(" printing items ");
            System.out.println(items);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            factory.close();
        }
    }

    public static ItemsD genItemsD(Items items) {
        ItemsD itemsD=new ItemsD();

        itemsD.setAssetId(items.getQuestionId());
        itemsD.setBody(items.getBody());
        itemsD.setLink(items.getLink());
        itemsD.setTitle(items.getTitle());
        itemsD.setTags(items.getTags());
        itemsD.setScore(items.getScore());
        itemsD.setOwnerLink(items.getOwner().getLink());
        itemsD.setProfileImage(items.getOwner().getProfileImage());
        itemsD.setDisplayName(items.getOwner().getDisplayName());

        AnswersD answersD = new AnswersD();
        answersD.setAssetId(items.getAcceptedAnswerId());

        for (Answers x: items.getAnswers()) {
            if (x.getIsAccepted()) {
                answersD.setBody(x.getBody());
                answersD.setCreationDate(x.getCreationDate());
                answersD.setScore(x.getScore());
                answersD.setDisplayName(x.getOwner().getDisplayName());
                answersD.setOwnerLink(x.getOwner().getLink());
                answersD.setProfileImage(x.getOwner().getProfileImage());
            }
        }

        itemsD.setAnswersD(answersD);

        return itemsD;
    }
}

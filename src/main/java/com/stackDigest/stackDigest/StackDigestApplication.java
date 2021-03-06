package com.stackDigest.stackDigest;

import com.stackDigest.stackDigest.beans.database.*;
import com.stackDigest.stackDigest.beans.restfetch.QuestionsAll.Answers;
import com.stackDigest.stackDigest.beans.restfetch.QuestionsAll.Items;
import com.stackDigest.stackDigest.beans.restfetch.QuestionsAll.JsonRootBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Calendar;

@SpringBootApplication
@EnableScheduling
public class StackDigestApplication implements ApplicationContextAware, BeanNameAware {

	private static SessionFactory factory;
	private ApplicationContext applicationContext;
	private String beanName;
	private int max=60;


	private int i=1;
	public static SessionFactory getFactory() {
		return factory;
	}

	@PostConstruct
	public void startUp() {

		factory=new Configuration().configure("hibernate.cfg.xml")
				.setProperty("hibernate.connection.url",System.getenv("JDBC_DATABASE_URL"))
				.setProperty("hibernate.connection.username",System.getenv("JDBC_DATABASE_USERNAME"))
				.setProperty("hibernate.connection.password",System.getenv("JDBC_DATABASE_PASSWORD"))
				.addAnnotatedClass(ItemsD.class)
				.addAnnotatedClass(OwnerD.class)
				.addAnnotatedClass(AnswersD.class)
				.addAnnotatedClass(UserD.class)
				.addAnnotatedClass(UserD_seen.class)
				.buildSessionFactory();
	}


	public static void main(String[] args) {
		SpringApplication.run(StackDigestApplication.class, args);
	}

	@PreDestroy
	public void shutdown() {
		System.out.println("Shut down");
		factory.close();
	}

	private void stopSchedulerTask() {
		ScheduledAnnotationBeanPostProcessor bean=applicationContext.getBean(ScheduledAnnotationBeanPostProcessor.class);
		bean.postProcessBeforeDestruction(this,beanName);
	}

	@Scheduled(fixedDelay = 1)
	public void delay() {

		System.out.println("hi");
		Session session= StackDigestApplication.getFactory().getCurrentSession();
		Transaction tx=session.beginTransaction();

		try {
			String uri = "https://api.stackexchange.com/2.2/search/advanced?page="+i+"&access_token=lcuewul2VbbADWgKoQAO2w))&key=65fR1xeD5oDJ8rNnDW7YtA((&order=desc&sort=votes&accepted=True&site=stackoverflow&filter=!)EhxQMOPc)dH94o7-NBjAb4AcHxu2_8*7Nua1q2CUHEgIfc*9";
			i++;
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
			JsonRootBean result = restTemplate.getForObject(uri, JsonRootBean.class);
			assert result != null;
			for (Items x:result.getItems()) {
//				System.out.println(genItemsD(x));
				session.save(genItemsD(x));
			}
			tx.commit();

			System.out.println(Calendar.getInstance().getTime()+" done");
			if(i==max) {
				//stop scheduling after certain iterations
				System.out.println("STOPPED SCHEDULING");
				stopSchedulerTask();
			}

			System.out.println("Database insertion "+(i-1)+" completed");
		}
		catch (Exception e) {
			max++;
			if (tx!=null)
				tx.rollback();
			e.printStackTrace();
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName=beanName;
	}
}

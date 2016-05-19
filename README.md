Puff JavaWeb Framework
======================
-------------

#简介#

##轻量级web开发框架##
- ###MVC###
#####基于servlet超薄封装，使用注解代替xml配置，支持多种视图（jsp、beetl、json）
```java
@Controller("/demo")
public class DemoController {
		public View jsp() {
			return ViewFactory.jsp("/demo.jsp");
		}
		public View json() {
			return ViewFactory.json("name", "Puff");
		}
		public View text(String name, int age) {
			return ViewFactory.text("my name is " + name + " my age is " + age + "");
		}
}
```



- ###ORM###
#####基于注解的简单ORM映射
```java
@Table("tb_demo")
class Demo {

		@Column
		@PrimaryKey(value = PKType.AUTO)
		private long id;
		
		@Column("demo_name")
		private String name;
		// getter setter
}
void crud() {
		SimpleExecutor executor = SimpleExecutor.getInstance();
		
		Demo d = new Demo();
		d.setName("test");
		executor.save(d);
		
		Demo demo = executor.queryByPk(Demo.class, "1");
		System.out.println(demo);
		
		demo.setName("test_update");
		executor.update(demo);
		
		executor.deleteByPK(Demo.class, "1");
		
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("demo_name", "test_update");
		executor.delete("tb_demo", columns);
		
		executor.delete("delete from tb_demo where demo_name=?", "puff");
		
		long count = executor.count("tb_demo");
		System.out.println(count);
		
		long count2 = executor.count(Demo.class);
		System.out.println(count2);
}
@Transaction
void transaction() {
		SimpleExecutor executor = SimpleExecutor.getInstance();
		executor.insert("insert into tb_demo (id) values (?) ", "888");
		executor.insert("insert into tb_demo (id) values (?) ", "8881");
}
```
- ###IOC
简单IOC实现注入

```java
@Bean(id = "cmsTemplateService")
public class CmsTemplateServiceImpl implements CmsTemplateService {
	//....
}



@Controller(value = "/admin/cms/template", scope = BeanScope.SINGLETON)
public class CmsTemplateController {
		@Inject
		private CmsTemplateService cmsTemplateService;
	
		public View delete() {
			List<String> list = PuffContext.getParameterList("ids", ",");
			if (list != null) {
				cmsTemplateService.deleteIn(list);
			}
			return ViewFactory.json(RetMsg.success("删除成功！！！"));
		}

```

- ###AOP###
```java

	@InterceptorChain({ @Before(LoginInterceptor.class), @Before(UserPermissionInterceptor.class) })
	public class CommonController {
	
		public CommonParam getCommonParam() {
			CommonParam p = new CommonParam();
			p.setPage(Integer.parseInt(StringUtil.empty(PuffContext.getParameter("page"), "1")));
			p.setRows(Integer.parseInt(StringUtil.empty(PuffContext.getParameter("rows"), "10")));
			p.setOrder(PuffContext.getParameter("order"));
			p.setSort(PuffContext.getParameter("sort"));
			return p;
		}
	}
	
	@Validate(CmsTemplateValidator.class)
			public View update() {
		
				CmsTemplate cms_template = PuffContext.getModel(CmsTemplate.class);
				cmsTemplateService.update(cms_template);
				
				return ViewFactory.json(RetMsg.success("更新成功！！！"));
			}
	}
```


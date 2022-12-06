package com.dzics.data.acquisition.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname SplitOrderPositionCfg
 * @Description 分别对不同订单创建独立的报工队列
 */
@Configuration
public class SplitOrderPositionCfg {

//            两米活塞杆1号岛	        DZ-1871
    @Value("${accq.1871.inner.product.position.query}")
    private String positionQuery1871;
    @Value("${accq.1871.inner.product.position.routing}")
    private String positionRouting1871;
    @Value("${accq.1871.inner.product.position.exchange}")
    private String positionExchange1871;

    @Bean(name = "queuePosition1871")
    public Queue queuePosition1871() {
        return new Queue(positionQuery1871, true);
    }

    @Bean(name = "exchangePosition1871")
    DirectExchange exchangePosition1871() {
        return new DirectExchange(positionExchange1871, true, false);
    }

    @Bean(name = "bindingPosition1871")
    Binding bindingPosition1871() {
        return BindingBuilder.bind(queuePosition1871()).to(exchangePosition1871()).with(positionRouting1871);
    }

//            两米活塞杆2号岛	        DZ-1872
    @Value("${accq.1872.inner.product.position.query}")
    private String positionQuery1872;
    @Value("${accq.1872.inner.product.position.routing}")
    private String positionRouting1872;
    @Value("${accq.1872.inner.product.position.exchange}")
    private String positionExchange1872;

    @Bean(name = "queuePosition1872")
    public Queue queuePosition1872() {
        return new Queue(positionQuery1872, true);
    }

    @Bean(name = "exchangePosition1872")
    DirectExchange exchangePosition1872() {
        return new DirectExchange(positionExchange1872, true, false);
    }

    @Bean(name = "bindingPosition1872")
    Binding bindingPosition1872() {
        return BindingBuilder.bind(queuePosition1872()).to(exchangePosition1872()).with(positionRouting1872);
    }

//            两米活塞杆3号岛	        DZ-1873
    @Value("${accq.1873.inner.product.position.query}")
    private String positionQuery1873;
    @Value("${accq.1873.inner.product.position.routing}")
    private String positionRouting1873;
    @Value("${accq.1873.inner.product.position.exchange}")
    private String positionExchange1873;

    @Bean(name = "queuePosition1873")
    public Queue queuePosition1873() {
        return new Queue(positionQuery1873, true);
    }

    @Bean(name = "exchangePosition1873")
    DirectExchange exchangePosition1873() {
        return new DirectExchange(positionExchange1873, true, false);
    }

    @Bean(name = "bindingPosition1873")
    Binding bindingPosition1873() {
        return BindingBuilder.bind(queuePosition1873()).to(exchangePosition1873()).with(positionRouting1873);
    }
//            两米活塞杆4号岛	        DZ-1874
    @Value("${accq.1874.inner.product.position.query}")
    private String positionQuery1874;
    @Value("${accq.1874.inner.product.position.routing}")
    private String positionRouting1874;
    @Value("${accq.1874.inner.product.position.exchange}")
    private String positionExchange1874;

    @Bean(name = "queuePosition1874")
    public Queue queuePosition1874() {
        return new Queue(positionQuery1874, true);
    }

    @Bean(name = "exchangePosition1874")
    DirectExchange exchangePosition1874() {
        return new DirectExchange(positionExchange1874, true, false);
    }

    @Bean(name = "bindingPosition1874")
    Binding bindingPosition1874() {
        return BindingBuilder.bind(queuePosition1874()).to(exchangePosition1874()).with(positionRouting1874);
    }
//            两米活塞杆5号岛	        DZ-1875
    @Value("${accq.1875.inner.product.position.query}")
    private String positionQuery1875;
    @Value("${accq.1875.inner.product.position.routing}")
    private String positionRouting1875;
    @Value("${accq.1875.inner.product.position.exchange}")
    private String positionExchange1875;

    @Bean(name = "queuePosition1875")
    public Queue queuePosition1875() {
        return new Queue(positionQuery1875, true);
    }

    @Bean(name = "exchangePosition1875")
    DirectExchange exchangePosition1875() {
        return new DirectExchange(positionExchange1875, true, false);
    }

    @Bean(name = "bindingPosition1875")
    Binding bindingPosition1875() {
        return BindingBuilder.bind(queuePosition1875()).to(exchangePosition1875()).with(positionRouting1875);
    }


//            两米活塞杆6号岛	        DZ-1876
    @Value("${accq.1876.inner.product.position.query}")
    private String positionQuery1876;
    @Value("${accq.1876.inner.product.position.routing}")
    private String positionRouting1876;
    @Value("${accq.1876.inner.product.position.exchange}")
    private String positionExchange1876;

    @Bean(name = "queuePosition1876")
    public Queue queuePosition1876() {
        return new Queue(positionQuery1876, true);
    }

    @Bean(name = "exchangePosition1876")
    DirectExchange exchangePosition1876() {
        return new DirectExchange(positionExchange1876, true, false);
    }

    @Bean(name = "bindingPosition1876")
    Binding bindingPosition1876() {
        return BindingBuilder.bind(queuePosition1876()).to(exchangePosition1876()).with(positionRouting1876);
    }
//            两米活塞杆7号岛	        DZ-1877
    @Value("${accq.1877.inner.product.position.query}")
    private String positionQuery1877;
    @Value("${accq.1877.inner.product.position.routing}")
    private String positionRouting1877;
    @Value("${accq.1877.inner.product.position.exchange}")
    private String positionExchange1877;

    @Bean(name = "queuePosition1877")
    public Queue queuePosition1877() {
        return new Queue(positionQuery1877, true);
    }

    @Bean(name = "exchangePosition1877")
    DirectExchange exchangePosition1877() {
        return new DirectExchange(positionExchange1877, true, false);
    }

    @Bean(name = "bindingPosition1877")
    Binding bindingPosition1877() {
        return BindingBuilder.bind(queuePosition1877()).to(exchangePosition1877()).with(positionRouting1877);
    }
//            三米活塞杆1号岛	        DZ-1878
    @Value("${accq.1878.inner.product.position.query}")
    private String positionQuery1878;
    @Value("${accq.1878.inner.product.position.routing}")
    private String positionRouting1878;
    @Value("${accq.1878.inner.product.position.exchange}")
    private String positionExchange1878;

    @Bean(name = "queuePosition1878")
    public Queue queuePosition1878() {
        return new Queue(positionQuery1878, true);
    }

    @Bean(name = "exchangePosition1878")
    DirectExchange exchangePosition1878() {
        return new DirectExchange(positionExchange1878, true, false);
    }

    @Bean(name = "bindingPosition1878")
    Binding bindingPosition1878() {
        return BindingBuilder.bind(queuePosition1878()).to(exchangePosition1878()).with(positionRouting1878);
    }
//            三米活塞杆2号岛	        DZ-1879
    @Value("${accq.1879.inner.product.position.query}")
    private String positionQuery1879;
    @Value("${accq.1879.inner.product.position.routing}")
    private String positionRouting1879;
    @Value("${accq.1879.inner.product.position.exchange}")
    private String positionExchange1879;

    @Bean(name = "queuePosition1879")
    public Queue queuePosition1879() {
        return new Queue(positionQuery1879, true);
    }

    @Bean(name = "exchangePosition1879")
    DirectExchange exchangePosition1879() {
        return new DirectExchange(positionExchange1879, true, false);
    }

    @Bean(name = "bindingPosition1879")
    Binding bindingPosition1879() {
        return BindingBuilder.bind(queuePosition1879()).to(exchangePosition1879()).with(positionRouting1879);
    }
//            三米活塞杆3号岛	        DZ-1880
    @Value("${accq.1880.inner.product.position.query}")
    private String positionQuery1880;
    @Value("${accq.1880.inner.product.position.routing}")
    private String positionRouting1880;
    @Value("${accq.1880.inner.product.position.exchange}")
    private String positionExchange1880;

    @Bean(name = "queuePosition1880")
    public Queue queuePosition1880() {
        return new Queue(positionQuery1880, true);
    }

    @Bean(name = "exchangePosition1880")
    DirectExchange exchangePosition1880() {
        return new DirectExchange(positionExchange1880, true, false);
    }

    @Bean(name = "bindingPosition1880")
    Binding bindingPosition1880() {
        return BindingBuilder.bind(queuePosition1880()).to(exchangePosition1880()).with(positionRouting1880);
    }
//            两米缸筒1号岛              DZ-1887
    @Value("${accq.1887.inner.product.position.query}")
    private String positionQuery1887;
    @Value("${accq.1887.inner.product.position.routing}")
    private String positionRouting1887;
    @Value("${accq.1887.inner.product.position.exchange}")
    private String positionExchange1887;

    @Bean(name = "queuePosition1887")
    public Queue queuePosition1887() {
        return new Queue(positionQuery1887, true);
    }

    @Bean(name = "exchangePosition1887")
    DirectExchange exchangePosition1887() {
        return new DirectExchange(positionExchange1887, true, false);
    }

    @Bean(name = "bindingPosition1887")
    Binding bindingPosition1887() {
        return BindingBuilder.bind(queuePosition1887()).to(exchangePosition1887()).with(positionRouting1887);
    }
//            两米缸筒2号岛	            DZ-1888
    @Value("${accq.1888.inner.product.position.query}")
    private String positionQuery1888;
    @Value("${accq.1888.inner.product.position.routing}")
    private String positionRouting1888;
    @Value("${accq.1888.inner.product.position.exchange}")
    private String positionExchange1888;

    @Bean(name = "queuePosition1888")
    public Queue queuePosition1888() {
        return new Queue(positionQuery1888, true);
    }

    @Bean(name = "exchangePosition1888")
    DirectExchange exchangePosition1888() {
        return new DirectExchange(positionExchange1888, true, false);
    }

    @Bean(name = "bindingPosition1888")
    Binding bindingPosition1888() {
        return BindingBuilder.bind(queuePosition1888()).to(exchangePosition1888()).with(positionRouting1888);
    }
//            两米缸筒3号岛	            DZ-1889
    @Value("${accq.1889.inner.product.position.query}")
    private String positionQuery1889;
    @Value("${accq.1889.inner.product.position.routing}")
    private String positionRouting1889;
    @Value("${accq.1889.inner.product.position.exchange}")
    private String positionExchange1889;

    @Bean(name = "queuePosition1889")
    public Queue queuePosition1889() {
        return new Queue(positionQuery1889, true);
    }

    @Bean(name = "exchangePosition1889")
    DirectExchange exchangePosition1889() {
        return new DirectExchange(positionExchange1889, true, false);
    }

    @Bean(name = "bindingPosition1889")
    Binding bindingPosition1889() {
        return BindingBuilder.bind(queuePosition1889()).to(exchangePosition1889()).with(positionRouting1889);
    }
//            三米缸筒1号岛	            DZ-1890
    @Value("${accq.1890.inner.product.position.query}")
    private String positionQuery1890;
    @Value("${accq.1890.inner.product.position.routing}")
    private String positionRouting1890;
    @Value("${accq.1890.inner.product.position.exchange}")
    private String positionExchange1890;

    @Bean(name = "queuePosition1890")
    public Queue queuePosition1890() {
        return new Queue(positionQuery1890, true);
    }

    @Bean(name = "exchangePosition1890")
    DirectExchange exchangePosition1890() {
        return new DirectExchange(positionExchange1890, true, false);
    }

    @Bean(name = "bindingPosition1890")
    Binding bindingPosition1890() {
        return BindingBuilder.bind(queuePosition1890()).to(exchangePosition1890()).with(positionRouting1890);
    }
//            三米缸筒2号岛	            DZ-1891
    @Value("${accq.1891.inner.product.position.query}")
    private String positionQuery1891;
    @Value("${accq.1891.inner.product.position.routing}")
    private String positionRouting1891;
    @Value("${accq.1891.inner.product.position.exchange}")
    private String positionExchange1891;

    @Bean(name = "queuePosition1891")
    public Queue queuePosition1891() {
        return new Queue(positionQuery1891, true);
    }

    @Bean(name = "exchangePosition1891")
    DirectExchange exchangePosition1891() {
        return new DirectExchange(positionExchange1891, true, false);
    }

    @Bean(name = "bindingPosition1891")
    Binding bindingPosition1891() {
        return BindingBuilder.bind(queuePosition1891()).to(exchangePosition1891()).with(positionRouting1891);
    }
//            两米活塞杆粗加工线	        DZ-1955
    @Value("${accq.1955.inner.product.position.query}")
    private String positionQuery1955;
    @Value("${accq.1955.inner.product.position.routing}")
    private String positionRouting1955;
    @Value("${accq.1955.inner.product.position.exchange}")
    private String positionExchange1955;

    @Bean(name = "queuePosition1955")
    public Queue queuePosition1955() {
        return new Queue(positionQuery1955, true);
    }

    @Bean(name = "exchangePosition1955")
    DirectExchange exchangePosition1955() {
        return new DirectExchange(positionExchange1955, true, false);
    }

    @Bean(name = "bindingPosition1955")
    Binding bindingPosition1955() {
        return BindingBuilder.bind(queuePosition1955()).to(exchangePosition1955()).with(positionRouting1955);
    }
//            三米活塞杆粗加工线	        DZ-1956
    @Value("${accq.1956.inner.product.position.query}")
    private String positionQuery1956;
    @Value("${accq.1956.inner.product.position.routing}")
    private String positionRouting1956;
    @Value("${accq.1956.inner.product.position.exchange}")
    private String positionExchange1956;

    @Bean(name = "queuePosition1956")
    public Queue queuePosition1956() {
        return new Queue(positionQuery1956, true);
    }

    @Bean(name = "exchangePosition1956")
    DirectExchange exchangePosition1956() {
        return new DirectExchange(positionExchange1956, true, false);
    }

    @Bean(name = "bindingPosition1956")
    Binding bindingPosition1956() {
        return BindingBuilder.bind(queuePosition1956()).to(exchangePosition1956()).with(positionRouting1956);
    }


}

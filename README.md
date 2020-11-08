# OpenGLDemo
Practice Project for OpenGL Learning

#### 滤镜demo

#### 培训课demo




#### 1.Android上OpenGL ES基本的类
OpenGL ES在Android开发上，是以GLSurfaceView为载体进行展示的

##### GLSurfaceView

```
GLSurfaceView glView = new GLSurfaceView (context);
	// 注意，记得给它设置版本，这里用OpenGL ES 2.0，那就设置version =2；
	glVIew.setEGLContextClientVersion(version);
	 // 重点，所有的绘制逻辑，基本全在这个Renderer里了；
	glView.setRenderer(renderer);
	 // 这个看情况用，不一定需要用这个，但一般会这么用。
	 // RENDERMODE_WHEN_DIRTY意思是只有你执行requestRender方法才去渲染，会比较节省性能，否则默认隔断时间就进行渲染。
	glVIew.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
```
关于GLSurfaceView的生命周期方法onResume和onPause分别在Activity的onResume和onPause分别调用。


##### GLSurfaceView.Renderer

Renderer是个接口，有三个方法需要实现。

```
 // 创建GLSurfaceView时回调的方法，主要做一些后面不会常用不变的字段进行初始化操作；
onSurfaceCreated(GL10 gl10,  EGLConfig config);
// 回调包括GLSurfaceView大小的变化或屏幕横竖变换；
onSurfaceChanged(GL10 gl10, int width, int height);
 // 看名字就知道，是绘制时会回调的方法，在这里做绘制逻辑；上面讲到requestRender，基本就是会来回调这个方法。
onDrawFrame(GL10 gl10);

```

##### Shader
中文人称：着色器。用来描述如何定坐标和渲染。用了一种类C语言的编程语言来写。主要有顶点(vertex)着色器和片段(fragment)着色器两种。。前者用来指定几何形状的顶点；后者用于指定每个顶点的着色。 每个GL程序必须要有一个vertex shader和一个fragment shader，且它们是相互对应的。（相互对应，意思是vertex shader必须要有一个fragment shader，反之亦然，但并不一定是一一对应）。基本上都是写OpenGL和这个两个shader打交道，通过shader去告诉OpenGL ES库，你想画在哪、填充什么颜色等等。所以，简单讲Renderer里主要是描述了和shader“打交道”的逻辑。

举一个简单的栗子：


```
// vertex shader
attribute vec4 aPosition; // 顶点坐标
uniform mat4 uMatrix; // 上面那个mMVPMatrix传进来就是这个东西
void main(){
     gl_Position = uMatrix * aPosition; // 最后算出最后的顶点坐标
}
// fragment shader
precision mediump float;
uniform vec4 uColor; // 填充的颜色
void main() {
    gl_FragColor = uColor;
}
```

简单来讲，顶点着色器用来确定坐标，片段着色器用来填充颜色或者纹理的。







---


在Android上Opengl是通过Vertex Shader 和 Fragment Shader 这两种定点着色器程序来实现图片的加载和渲染的，中文称为定点着色器和片段着色器。一个完整的Opengl程序需要创建定点着色器和片段着色器并将他们Link起来组成一个完整的OpenGL程序。

　　顶点着色器的作用是为每一个顶点生成坐标，因此每个顶点都要运行一遍顶点着色器程序，一旦顶点坐标计算出来之后，OpenGL就能够使用这些顶点来组成点，线，和三角形。所有任意的图形都是由这三种基本元素来描述的。下图是顶点着色器进行坐标转换的过程（稍微有点复杂）：
　　
　　![image](https://images0.cnblogs.com/blog2015/306217/201505/022132338331597.png)
　　
　　
　　
```
private static final String VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
        "attribute vec2 a_texcoord;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_Position = a_position;\n" +
        "  v_texcoord = a_texcoord;\n" +
        "}\n";

    private static final String FRAGMENT_SHADER =
        "precision mediump float;\n" +
        "uniform sampler2D tex_sampler;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
        "}\n";
```


　先看一下VERTEX_SHADER，定义了两种类型的变量 atrribute 和 varying。

　　其中 attribute变量是只能在vertex shader中使用的变量。（它不能在fragment shader中声明attribute变量，也不能被fragment shader中使用），一般用attribute变量来表示一些顶点的数据，这些顶点数据包含了顶点坐标，法线，纹理坐标，顶点颜色等。应用中一般用函数glBindAttribLocation（）来绑定每个attribute变量的位置，然后用函数glVertexAttribPointer()为每个attribute变量赋值。

　　varying被称为易变量，一般用于从Vertex Shader 向 Fragment Shader传递数据，上面例子中在VertexShader中定义了attribute 类型的二维向量a_texcoord，并将该值赋值给varying类型的二维向量 v_texcoord。此外对于Vertex Shader 在main() 中必须将顶点坐标赋值给系统变量gl_Position。
　　
　　
　　
　　vertex shader

```
private const val VERTEX_SHADER =
                "void main() {\n" +
                        "gl_Position = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                        "gl_PointSize = 20.0;\n" +
                        "}\n"
```

shader语言跟C语言很像，它有一个主函数，也叫void main(){}。
gl_Position是一个内置变量，用于指定顶点，它是一个点，三维空间的点，所以用一个四维向量来赋值。vec4是四维向量的类型，vec4()是它的构造方法。等等，三维空间，不是（x, y, z）三个吗？咋用vec4呢？ 四维是叫做齐次坐标，它的几何意义仍是三维，先了解这么多，记得对于2D的话，第四位永远传1.0就可以了。这里，是指定原点(0, 0, 0)作为顶点，就是说想在原点位置画一个点。gl_PointSize是另外一个内置变量，用于指定点的大小。
这个shader就是想在原点画一个尺寸为20的点。


fragment shader

```
private const val FRAGMENT_SHADER =
                "void main() {\n" +
                        "gl_FragColor = vec4(1., 0., 0.0, 1.0);\n" +
                        "}\n"
```
gl_FragColor是fragment shader的内置变量，用于指定当前顶点的颜色，四个分量（r, g, b, a）。这里是想指定为红色，不透明。

　　

　　看一下FRAGMENT_SHADER，定义了两种类型的变量，uniform 和 varying。此外还多了一句 precision mediump float，这句话用于定义数据精度，Opengl中可以设置三种类型的精度（lowp,medium 和 highp），对于Vertex Shader来说，Opengl使用的是默认最高精度级别（highp），因此没有定义。

　　uniform变量是APP序传递给（vertex和fragment）shader的变量。通过函数glUniform**（）函数赋值的。 在（vertex和fragment）shader程序内部，uniform变量就像是C语言里面的常量（const ），它不能被shader程序修改（shader只能用，不能改）。如果uniform变量在vertex和fragment两者之间声明方式完全一样，则它可以在vertex和fragment共享使用。 （相当于一个被vertex和fragment shader共享的全局变量）uniform变量一般用来表示：变换矩阵，材质，光照参数和颜色等信息。








---



[Android OpenGL 基础入门](https://www.cnblogs.com/zhuyp1015/p/4472599.html)

[Android OpenGL 编写简单滤镜](https://www.cnblogs.com/zhuyp1015/p/4513355.html)


[视频编辑技术文章和OpenGL学习资料汇总](http://www.zwdroid.top/2017/03/18/Video%20edit%20tech%20and%20OpenGL%20materials/)


[Android OpenGL ES开发初探](https://cloud.tencent.com/developer/article/1164128)


[年轻人的第一篇OpenGL ES 2.0教程](http://toughcoder.net/blog/2018/07/31/introduction-to-opengl-es-2-dot-0/)

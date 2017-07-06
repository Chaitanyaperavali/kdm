import java.io.File

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF, Word2Vec, Word2VecModel}

import scala.collection.immutable.HashMap

/**
  * Created by Mayanka on 19-06-2017.
  */
object TF_IDF {
  def main(args: Array[String]): Unit = {

    System.setProperty("hadoop.home.dir", "E:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    //Reading the Text File
    val documents = sc.textFile("data/Article.txt")
    //val documents1 = sc.wholeTextFiles("data")

    //Getting the Lemmatised form of the words in TextFile
    val documentseq = documents.map(f => {
      val lemmatised = CoreNLP.returnLemma(f)
      val splitString = f.split(" ")
      splitString.toSeq
    })

    //Creating an object of HashingTF Class
    val hashingTF = new HashingTF()

    //Creating Term Frequency of the document
    val tf = hashingTF.transform(documentseq)
    tf.cache()


    val idf = new IDF().fit(tf)

    //Creating Inverse Document Frequency
    val tfidf = idf.transform(tf)

    val tfidfvalues = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    val tfidfindex = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    //tfidf.foreach(f => println(f))  //commented

    val tfidfData = tfidfindex.zip(tfidfvalues)

    var hm = new HashMap[String, Double]

    tfidfData.collect().foreach(f => {
      hm += f._1 -> f._2.toDouble
    })

    val mapp = sc.broadcast(hm)

    val documentData = documentseq.flatMap(_.toList)
    val dd = documentData.map(f => {
      val i = hashingTF.indexOf(f)
      val h = mapp.value
      (f, h(i.toString))
    })

    val modelFolder = new File("myModelPath")

    val input = documentseq

    if (modelFolder.exists()) {
      val dd1 = dd.distinct().sortBy(_._2, false)
      dd1.take(10).foreach(f => {

        val sameModel = Word2VecModel.load(sc, "myModelPath")
        val synonyms = sameModel.findSynonyms(f._1, 10) //change here
        println("---------------------synonyms for "+f._1+"\n---------------------")
        for ((synonym, cosineSimilarity) <- synonyms) {
          println(s"$synonym $cosineSimilarity")
        }
        //println(f)
      })
    }

    else{
      val dd1 = dd.distinct().sortBy(_._2, false)
      dd1.take(10).foreach(f => {
      val word2vec = new Word2Vec().setVectorSize(1000)

      val model = word2vec.fit(input)

      val synonyms = model.findSynonyms(f._1, 10)

      for ((synonym, cosineSimilarity) <- synonyms) {
        println(s"$synonym $cosineSimilarity")
      }

      model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))

      // Save and load model
      model.save(sc, "myModelPath")
    })
    }



  }

}

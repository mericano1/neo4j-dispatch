import dispatch._
import dispatch.liftjson.Js._
import net.liftweb.json._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonParser


case class NeoResult(columns:List[String], data:List[List[String]])

/*

This object has some methods to figure out separate graphs in the dependency graph

*/
object NeoQuery {
  // Http request object
  val req = url("http://localhost:7474/db/data/cypher") <:< Map("Content-type" -> "application/json", "Accept" -> "application/json") 

  // default formats used by lift json to parse dates and other objects
  implicit val formats = DefaultFormats

  object Query {
    //neo4j query structure
    def forNeo(s:String) = """{"query" : "%s","params" : {}}""".format(s)
  
    def connectedNodesQ(id:Int) = "start a = node(%d) match a--b return b.name".format(id)
    val contendedNodesQ = " start a=node(*) match ()--r--> a with r, a, count(*) as inComings where inComings > 1 return distinct ID(a), a.name order by(a.name) "
    def graphForNodeQ(nodeId: Int):String = "start b  = node(%d) match p = (a-[*]-b) return distinct a.name".format(nodeId)
    val allNodesQ = "start a=node(*) return ID(a)"
  }

  def main(args: Array[String]) {
    val graphs = getAllEdgesForNodes(getAllNodes.data)
    val squashed = graphs.tail.foldRight(List(graphs.head))(joinIfOverlap)

    println(squashed.mkString("\n"))
    Http.shutdown()
  }

  def joinIfOverlap[T](l2:List[T], l1:List[List[T]]):List[List[T]] = {
    val s2:Set[T] = l2.toSet
    l1.partition(l => (l.toSet & s2) != Set.empty) match { 
       case (List(), dm) => l2 :: dm
       case (m,dm) =>  (l2::m).flatten.distinct :: dm
    }
  }

  def getAllEdgesForNodes(nodes:List[List[String]]) = nodes.map{ 
    case id::property => { 
      import Query._
      Http((req << forNeo(connectedNodesQ(id.toInt))) ># {jsonGraph => {
        val result = jsonGraph.extract[NeoResult]
        (property :: result.data).flatten
        }
      })
    }
    case _ => List()
  }

  def getAllNodes() = {
    import Query._
    Http((req << forNeo(allNodesQ)) ># {jsonStr => jsonStr.extract[NeoResult]})
  }
}

package project2

import akka.actor._
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.actor.ActorRef
import scala.util.Random


object Gossip{
    sealed trait Message 
    case class setneighbor(top : String, i : Int, nrofnd : Int, diednodes : Array[Int]) extends Message
    case class targetneighbor(target : String) extends Message
    case class targetneighbor_putsum(target : String, s : Double, w : Double) extends Message
    case class receivemsg(sx : Double, wx : Double) extends Message
    case class time(t : Long) extends Message
    
    abstract class Node extends Actor {
        var neighbors :  Array[String] = Array.empty
        def fun(x : Int, y : Int, z : Int, L : Int) : String = {
            var sum = x + (y - 1) * L + (z - 1) * L * L
            return sum.toString      
        }
        def topbuilder(top : String, i : Int, n : Int, diednodes : Array[Int]) = {
                var m = diednodes.length
//For Debug     //println("m " + m)
                //println(self.path.name)
                //for(i <- 1 to diednodes.length){print(diednodes(i - 1), " ")}
                
                if(top == "line"){
//For Debug         //println("top", i)
                    if(i == 1){
                        if((diednodes contains 2) == false){neighbors = neighbors ++ Array("2")} 
                    }                        
                    else if(i == n) {
                        if((diednodes contains (n - 1)) == false){neighbors = neighbors ++ Array((i - 1).toString)}  
                        }
                    else{
                        if((diednodes contains (i - 1)) == false){neighbors = neighbors ++ Array((i - 1).toString)}
                        if((diednodes contains (i + 1)) == false){neighbors = neighbors ++ Array((i + 1).toString)}
                    }
                    for(j <- 0 to neighbors.length - 1){
//For Debug             //println("node : ", i, "neighbors", neighbors(j), "length", neighbors.length)
                    }
                }
                else if(top == "full"){
                    for(j <- 1 to n){
                        if(j! = i){
                            if((diednodes contains (j)) == false){neighbors = neighbors ++ Array((j).toString)}
                        }
                    }
                    for(j <- 0 to neighbors.length - 1){
//For Debug             //println("node : ", i, "neighbors", neighbors(j), "length", neighbors.length)
                        }                
                }
                else if(top == "3d" || top == "imp3d"){
                    var L = scala.math.pow(n, 1.0/3.0).toInt
                    var i1 = i - 1
                    var z = (i1 - (i1%(L * L))) / (L * L) + 1
                    var y = ((i1 - (z - 1) * (L * L)) - ((i1 - (z - 1) * (L * L))%(L)))/(L) + 1
                    var x = (i1 - (z - 1) * (L * L)) - (y - 1) * L + 1
                    var x0 = 0; var y0 = 0;var z0 = 0
                    x0 = x + 1
                    if(x0 >= 1 && x0 <= L){
                        if((diednodes contains fun(x0, y, z, L)) == false){
                            neighbors = neighbors ++ Array(fun(x0, y, z, L))
                        }
                    }
                    x0 = x - 1
                    if(x0 >= 1 && x0 <= L){
                        if((diednodes contains fun(x0, y, z, L)) == false){
                            neighbors = neighbors ++ Array(fun(x0, y, z, L))
                        }
                    } 
                    y0 = y + 1
                    if(y0 >= 1 && y0 <= L){
                        if((diednodes contains fun(x, y0, z, L)) == false){
                            neighbors = neighbors ++ Array(fun(x, y0, z, L))
                        }
                    }                  
                    y0 = y - 1
                    if(y0 >= 1 && y0 <= L){
                        if((diednodes contains fun(x, y0, z, L)) == false){
                            neighbors = neighbors ++ Array(fun(x, y0, z, L))
                        }
                    }
                    z0 = z + 1
                    if(z0 >= 1 && z0 <= L){
                        if((diednodes contains fun(x, y, z0, L)) == false){
                            neighbors = neighbors ++ Array(fun(x, y, z0, L))
                        }
                    }                  
                    z0 = z - 1
                    if(z0 >= 1 && z0 <= L){
                        if((diednodes contains fun(x, y, z0, L)) == false){
                            neighbors = neighbors ++ Array(fun(x, y, z0, L))
                        }
                    }
                    if(top == "imp3d"){
                        var existflag = true
                        while(existflag == true){
                            var rand = new Random
                            var ranneighbor = (rand.nextInt(n - 1) + 1).toString
                            existflag = neighbors contains ranneighbor
                            if(i.toString == ranneighbor){existflag = true}
                            if(existflag == false){
                                if((diednodes contains ranneighbor) == false){neighbors = neighbors ++ Array(ranneighbor)}
                            }
                        }
                    }
                    for(j <- 0 to neighbors.length - 1){
//For Debug             //println("node : ", i, "neighbors", neighbors(j), "length", neighbors.length)
                    }                                                                                                                                                                                                                          
                }                                                                      
        }        
    }    
    
    class Gossipnode extends Node{    
        var counter = 0
        val maxcounter = 10
        var heard = false
        def receive = {
            case setneighbor(top, i, n, diednodes)  =>
                topbuilder(top, i, n, diednodes)
                sender ! "neighbors set finished" 
            case "spreadrumor"  =>
                if (heard == true & counter<maxcounter){
                    val rand = new Random
                    val randn = rand.nextInt(neighbors.length)
//For Debug         //println(self.path.name, "randn", "#", randn, " : ", neighbors(randn))
//For Debug         //println(self.path.name, " - >", neighbors(randn))
                    sender ! targetneighbor(neighbors(randn)) 
                } 
            case "gossiping"  =>                              
                if (counter<maxcounter) {
                heard = true
                counter = counter + 1 
                //println("heard gossip", self.path.name, counter)
                sender ! "continue"}
                if (counter == maxcounter){sender ! "finish"}
            case "finishgossip"  => sender ! "continue"
            case "haverumor"  =>
                  heard = true
                  //println("haverumor", self.path.name, counter)     
            case _  => println("Gossipnode")
        }
    }
    
    class Pushsumnode extends Node{
        var s : Double = 0.0
        var w : Double = 1.0
        var s0 = s
        var w0 = w
        var convflag : Double = 10e - 10
        var convcounter = 0
        var maxconvcounter = 3
        var diff : Double = 0.0
        def receive = {
            case setneighbor(top, i, n, diednodes)  =>
                var seed = new Random
                s = i
                s = seed.nextGaussian()
                s0 = s
                topbuilder(top, i, n, diednodes)
                sender ! "neighbors set finished" 
            case "sendmsg"  =>
                if(convcounter < maxconvcounter){
                    s0 = s
                    w0 = w
                    s = s / 2.0
                    w = w / 2.0
                    val rand = new Random
                    val randn = rand.nextInt(neighbors.length)
                    sender ! targetneighbor_putsum(neighbors(randn), s, w)                                 
                }
            case receivemsg(sx, wx)  =>
                    s = s + sx
                    w = w + wx
                    diff = scala.math.abs(s / w - s0 / w0)
                    if(diff>convflag){convcounter = 0}
                    else if(diff <= convflag){convcounter = convcounter + 1}
                    if(convcounter<maxconvcounter){
                        sender ! "continue_putsum"
                    }
                    else if (convcounter == maxconvcounter){
                        println(self.path.name, s / w)
                        sender ! "finish"
                    }

            case _  => println("Pushsumnode")
        }
    }
    
    class Network(nrofnd : Int, top : String, algo : String, nrdies : Int) extends Actor {
        val n = nrofnd
        val m = nrdies
        var nodesetcounter = 0
        var finishcounter = 0
        var start = System.currentTimeMillis
        val system  = ActorSystem("mysystem") 
        var diednodes : Array[Int] = new Array[Int](m)
        var j = 0
        while(j<nrdies){
             val randx = new Random
             var randxx = randx.nextInt(n - 1) + 1
             //println("randxx " + randxx)
             if((diednodes contains randxx) == false) {
                //println("j = " + j + " :  " + randxx + " in")
                diednodes(j) = randxx
                j = j + 1              
             }
        }        
        val rand = new Random
        var randi = rand.nextInt(n - 1) + 1
        while((diednodes contains randi) == true){
            randi = rand.nextInt(n - 1) + 1
        }
        print("Died nodes :  ")
        for(i <- 1 to m){print(diednodes(i - 1) + " ")}
        println("")
        if(algo == "gossip"){
            for(i <- 1 to n){
                if((diednodes contains i) == false){
                    val master = system.actorOf(Props(new Gossipnode), name = "gossip" + i.toString)                
                }                 
            }
            val rumorholder = system.actorSelection("/user/" + algo + randi.toString)
            rumorholder ! "haverumor"
            println("Node" + randi.toString + " have rumor")            
        }
        else if (algo == "pushsum"){                                   
            for(i <- 1 to n){
                if((diednodes contains i) == false){
                    val master = system.actorOf(Props(new Pushsumnode), name = "pushsum" + i.toString)
                }
            }    
        }
   
        
        for(i <- 1 to n){
            if((diednodes contains i) == false){            
                system.actorSelection("/user/" + algo + i) ! setneighbor(top, i, n, diednodes) //Set neighbors
            }
        }
                

        def receive = { 
            case "neighbors set finished"  =>
                nodesetcounter = nodesetcounter + 1
                if(nodesetcounter == nrofnd - nrdies) {
                    println("Topology building finished")
                    val b = System.currentTimeMillis
                    if(algo == "gossip"){
                        for(i <- 1 to n){
                            if((diednodes contains i) == false){ 
                                system.actorSelection("/user/" + algo + i.toString) ! "spreadrumor"
                            }
                        }
                    }
                    else if(algo == "pushsum"){
                        for(i <- 1 to n){
                            if((diednodes contains i) == false){
                                system.actorSelection("/user/" + algo + i.toString) ! "sendmsg"
                            }
                        }                    
                    }
                    self ! time(b)                
                }
            case targetneighbor(target) =>
                system.actorSelection("/user/" + algo + target) ! "gossiping"
                sender ! "finishgossip"
            case targetneighbor_putsum(target, s, w)  =>
                system.actorSelection("/user/" + algo + target) ! receivemsg(s, w)
            case "continue" =>
                sender ! "spreadrumor"
            case "continue_putsum" =>
                    sender ! "sendmsg"
            case "finish"  =>
                finishcounter = finishcounter + 1
                if(finishcounter == 1) { //Terminate when one finishcounter
                    println(algo.toUpperCase() + " Finish !!")
                    println("Time spent :  " + ( - start + System.currentTimeMillis) + " ms")
                    context.system.terminate()
                }
           
            case time(b)  => start = b 
            case _  => println("Network")  
        }
    }  
    
    def main(args :  Array[String]) {
        try{
            var nrofnd = args(0).toInt  // # of nodes
            var top = args(1).toLowerCase() // topology
            var algo = args(2).toLowerCase() //algorithm
            var nrdies = args(3).toInt //# of died nodes
            val system  = ActorSystem("mysystem")

            if(nrofnd < 2){
                println("NumNodes must be at least 2")
                sys.exit(0)   
            }

             
            if(top == "3d" || top == "imp3d"){
                nrofnd = scala.math.pow(scala.math.ceil(scala.math.pow(nrofnd, 1.0/3.0)), 3.0).toInt
            }

//          val algomap = Map("gossip" - >(()  =>new Gossipnode), "pushsum" - >(()  =>new Pushsumnode))
            val topbuild = top match{
                case "line"  => system.actorOf(Props(new Network(nrofnd, top, algo, nrdies)), name = "topbuild")
                case "full"  => system.actorOf(Props(new Network(nrofnd, top, algo, nrdies)), name = "topbuild")
                case "3d"  => system.actorOf(Props(new Network(nrofnd, top, algo, nrdies)), name = "topbuild")
                case "imp3d"  => system.actorOf(Props(new Network(nrofnd, top, algo, nrdies)), name = "topbuild")
            }
            
        }
        catch{
            case _  : Throwable  =>
            println("Wrong inputs!!")
            println("Usage :  run NumNodes Topology Algorithm") 
            println("Topology must be {line, full, 3d, imp3d}")
            println("Algorithm must be {gossip, pushsum}")             
        }
    }
}









package weka.api;
import weka.clusterers.SimpleKMeans; 

// In weka class we import simplekmeans - means and its method that will helps us 
// to implemnt k-means clustering algorithum

import weka.core.Instances; 
// Here we use class data source to load the dataset Intances to store all the data value of the dataset.

import weka.core.Instance; 
// Here we use class Instance to all single Intance.

import weka.core.converters.ConverterUtils.DataSource; 
import java.util.*;

public class project1 
{
  public static void main(String[] args) throws Exception
  {
	  int k;   // value of k in k means clustering.
	  Scanner sc = new Scanner( System.in );
	  System.out.print(" enter num of cluster =  ");
	  k = sc.nextInt();
    
	  double centroidArrayX[] = new double[k];    // create centroidArrayX of X coordinate storing centorid of k cluster.
	  double centroidArrayY[] = new double[k];    // create centeoidArrayY of Y corrdinate storing centorid of k cluster.
	  double clusterMaxDistance[] = new double[k];
    
	  int optimalTower[] = new int[10];
	  boolean isClusterAlloted[] = new boolean[k];
    
	 // boolean array to check the allocate cluster to tower
	  ArrayList<Integer>[] clusterList = new ArrayList[k]; 
    
	  // create clusterlist to keep track of tower in range
      for ( int j = 0; j < k; j++ )
      { 
    	  clusterList[j] = new ArrayList<Integer>();
      }
      
      int i = 0;
      
	   // Loading Population Dataset.
     // First we load the dataset of population and we calculate the instances 
     // object with all the coordinate point.
      
	  String dataset = "population.arff";
	  DataSource source = new DataSource( dataset );
	  Instances data = source.getDataSet();
    
	 // bulid k cluster for population data set.
	  SimpleKMeans model = new SimpleKMeans();
	  model.setNumClusters( k );
	  model.buildClusterer( data );
	  System.out.print( model );
	  
	  // Here we store centroids for each cluster. 
	  Instances centroids = model.getClusterCentroids();
	  for( Instance cent:centroids )
	  {
		  centroidArrayX[i] = cent.value(0);
		  centroidArrayY[i++] = cent.value(1);
	  }   

	  // find distance from centroids to fastest node in each cluster to compare it
	  // with the range of tower for better network.
    
	  Instances testData = DataSource.read(dataset);
	  for( Instance point: testData ) 
	  {
		 int clusterNo = model.clusterInstance( point );
		 double distance = ( (point.value(0)-centroidArrayX[clusterNo])(point.value(0)-centroidArrayX[clusterNo]) ) + ( (point.value(1)-centroidArrayY[clusterNo])(point.value(1)-centroidArrayY[clusterNo]) );
		 distance = Math.sqrt(distance);
		 if( clusterMaxDistance[clusterNo] < distance )
			 clusterMaxDistance[clusterNo] = distance;
		 // Here we are storing the distance in the clusterMaxDistance of the particular cluster.
	  }
	  
	  // Loading TowerLocation Dataset with given radius.
	  
	  i = 0;
	  dataset = "towerlocation.arff";
	  Instances towerLocations = DataSource.read(dataset);
	  for(Instance tower:towerLocations)
	  {
		  for( int itr = 0; itr < k; itr++ )
		  {
			// calculating distance b/w centroid and tower
			  double range = Math.sqrt( ( ( tower.value(0) - centroidArrayX[itr] ) * ( tower.value(0) - centroidArrayX[itr] ) ) + ( ( tower.value(1) - centroidArrayY[itr] ) * ( tower.value(1) - centroidArrayY[itr] ) ) );
        
	        //comparing the ( distance from centroid to farthest node) + ( distance between centroid and towers) with
          // the keeping track of all the tower location in the optimaltower array of optimal result.
          
			  if( ( clusterMaxDistance[itr] + range ) <= tower.value(2) )
			  {
				  optimalTower[i]++;	
				  clusterList[itr].add(i);
			  }
			  
			  continue;
		  }
		  
		  i++;
	  }
	  
	   // Here we create towerlist
	  ArrayList< Integer > towerList = new ArrayList< Integer >(); 
    
      for( i = 0; i < optimalTower.length; i++ ) 
          towerList.add( optimalTower[i] ); 
          
	  int max = Collections.max( towerList );
	  String str;
	  int flag = 0;
	  ArrayList< Integer > falseAffected = new ArrayList<Integer>(); 
    
	  while( max > 1 )
	  {
		  str="";
		  for( i = 0; i < k; i++ )
		  {
			  if( clusterList[i].contains( towerList.indexOf( max ) ) )
		      {
		    	  if( isClusterAlloted[i] == false )
				  {
		    		  str += "Cluster " + i + " ";
		    		  isClusterAlloted[i] = true;
		    		  falseAffected.add( i );
				  }
		    	  
		    	  else
				  {
					  flag = 1;
					  for( int j = 0; j <falseAffected.size(); j++ )
						  isClusterAlloted[ falseAffected.get(j) ] = false;
					  break;
				  }
		      }	  
			  
			  continue;
		  }
		  
		  if( flag == 0 )
		  {
			  System.out.println( str+" ------- tower location "+towerLocations.instance( towerList.indexOf( max ) ).value( 0 )+","+towerLocations.instance( towerList.indexOf( max ) ).value( 1 ) );
		  }
		  
		  towerList.set( towerList.indexOf(max), 0 );
		  flag = 0;
		  falseAffected.clear();
		  max = Collections.max( towerList );
	  }
	  for( i = 0; i < k; i++ )
	  {
		  if( isClusterAlloted[i] == false )
		  {
			  str = " Cluster " + i + " ------ tower Locations ";
			  for( int j = 0; j < clusterList[i].size(); j++ )
			  {
				  str += towerLocations.instance( clusterList[i].get(j) ).value( 0 ) + ","  + towerLocations.instance( clusterList[i].get( j ) ).value( 1 ) + "   ";
			  }
			  
			  System.out.println( str );
		  }
	  }
  }
}

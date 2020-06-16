package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.data.model.relationship.DataSetWithCredentials

@Dao
interface DataSetDAO {

    @Transaction
    @Query("SELECT * FROM dataSet_ Where :hashData = hashData")
    suspend fun getDataSetByHash(hashData: String): DataSetWithCredentials?

    @Transaction
    @Query("SELECT * FROM dataSet_ Where dataSetId=:id")
    suspend fun getDataSetByDataSetID(id: Long): DataSetWithCredentials?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertDataSet(vararg dataSet: DataSet): List<Long>

    @Query("Delete from dataSet_")
    suspend fun deleteAllDataSets()


    @Query("Select d.dataSetName,d.dataSetId from dataSet_ d where d.serviceId=:serviceId ")
    fun publicGetAllDataSetsByServiceId(serviceId: Long): LiveData<List<LayoutDataSetView>>


    @Transaction
    @Query("SELECT * FROM dataSet_")
    fun getDataSetWithCredentials(): List<DataSetWithCredentials>


    @Transaction
    @Query("SELECT * FROM dataSet_ where dataSetId =:dataSetId")
    suspend fun getDataSetWithCredentialsByDataSetID(dataSetId: Long): DataSetWithCredentials?

    @Delete
    suspend fun deleteDataSet(vararg dataSet: DataSet)


    @Query("SELECT * FROM dataSet_")
    fun publicGetAllDataSet(): List<DataSet>

}
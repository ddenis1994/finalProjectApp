package com.example.finalprojectapp.credentialsDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView

import com.example.finalprojectapp.data.model.relationship.DataSetCredentialsManyToMany
import com.example.finalprojectapp.data.model.relationship.DataSetWithCredentials
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel

@Dao
interface DataSetDAO {


    @Query("SELECT * FROM dataSet_ Where :hashData = hashData")
    suspend fun getDataSetByHash(hashData: String): DataSetWithCredentials?

    @Query("SELECT * FROM dataSet_ Where dataSetId=:id")
    suspend fun getDataSetByDataSetID(id: Long): DataSetWithCredentials?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertDataSet(vararg dataSet: DataSet): List<Long>


    // TODO: 15/06/2020 remove go to one to many
    @Update
    fun privateUpdateNewCre(vararg newManyToMany: DataSetCredentialsManyToMany): Int

    // TODO: 15/06/2020 remove go to one to many
    @Query("SELECT r.DataSetCredentialsManyToManyID FROM  dataSetCredentialsManyToMany r , credentials_ c   Where :dataSetID = r.dataSetId AND r.credentialsId = c.credentialsId And c.hint Like :hints")
    suspend fun privateGetUnionDataSetAndCredentialsHash(dataSetID: Long, hints: String): Long?



    @Query("Delete from dataSet_")
    suspend fun deleteAllDataSets()

    // TODO: 15/06/2020 remove go to one to many
    @Query("DELETE FROM dataSetCredentialsManyToMany  WHERE dataSetId=:dataSetId")
    suspend fun deleteFromRelationship(dataSetId: Long): Int

    @Query("Select d.dataSetName,d.dataSetId from dataSet_ d where d.serviceId=:serviceId ")
    fun publicGetAllDataSetsByServiceId(serviceId: Long): LiveData<List<LayoutDataSetView>>

    // TODO: 15/06/2020 remove go to one to many
    @Query("Select c.iv,c.data,c.hint,c.credentialsId from dataSetCredentialsManyToMany r,credentials_ c where r.dataSetId=:dataSetId and r.credentialsId = c.credentialsId")
    fun publicGetAllCredentialsByDataSetID(dataSetId: Long): LiveData<List<LayoutCredentialView>>


    @Query("SELECT * FROM dataSetCredentialsManyToMany Where dataSetId =:num ")
    suspend fun privateGetDataSetToCredentials(num: Long): List<DataSetCredentialsManyToMany>


    @Transaction
    @Query("SELECT * FROM dataSet_")
    fun getDataSetWithCredentials(): List<DataSetWithCredentials>


    @Transaction
    @Query("SELECT * FROM dataSet_ where dataSetId =:dataSetId")
    suspend fun getDataSetWithCredentialsByDataSetID(dataSetId: Long): DataSetWithCredentials?


    // TODO: 15/06/2020 remove go to one to many
    @Query("SELECT * FROM dataSetCredentialsManyToMany  Where :dataSetId like dataSetId")
    fun findAllRelationshipToDataSet(vararg dataSetId: Long): List<DataSetCredentialsManyToMany>

    // TODO: 15/06/2020 remove go to one to many
    @Delete
    fun deleteDataSetRelationship(vararg dataSetCredentialsManyToMany: DataSetCredentialsManyToMany)

    @Delete
    suspend fun deleteDataSet(vararg dataSet: DataSet)

    // TODO: 15/06/2020 remove go to one to many
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun privateInsertCredentials(dataSetCredentialsManyToMany: DataSetCredentialsManyToMany): Long

    // TODO: 15/06/2020 remove go to one to many
    @Query("DELETE FROM dataSetCredentialsManyToMany  WHERE credentialsId=:credentialId and dataSetId=:dataSetId")
    fun deleteFromRelationshipCredential(credentialId: Long, dataSetId: Long): Int

    // TODO: 15/06/2020 fix this before delete
    @Transaction
    @Query("select  r.credentialsId,c.innerHashValue hash, r.dataSetId dataSet from  credentials_ c,dataSetCredentialsManyToMany r where r.credentialsId=c.credentialsId and c.hint like '%Password%' or '%password%'   ")
    fun publicGetAllHashCredentials(): LiveData<List<DashboardViewModel.HashAndId>>

    // TODO: 15/06/2020 fix this before delete
    @Transaction
    @Query("select  c.credentialsId,c.innerHashValue hash, c.credentialDataSetId dataSet from  credentials_ c where  c.hint like '%Password%' or '%password%'   ")
    fun publicGetAllHashCredentials2(): List<DashboardViewModel.HashAndId>?

    // TODO: 15/06/2020 remove go to one to many
    @Query("SELECT * FROM dataSetCredentialsManyToMany  Where credentialsId=:credentialID")
    fun privateGetRelationshipCredential(credentialID: Long): List<DataSetCredentialsManyToMany>


    @Query("SELECT * FROM dataSet_")
    fun publicGetAllDataSet(): List<DataSet>

}
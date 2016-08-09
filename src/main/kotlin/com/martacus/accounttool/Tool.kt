package com.martacus.accounttool

import com.google.gson.*
import javafx.collections.FXCollections
import javafx.scene.control.ContextMenu
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import tornadofx.*
import java.io.*

class Tool : App(){
    override val primaryView = ToolView::class
}


@Suppress("JAVA_CLASS_ON_COMPANION")
class ToolView : View() {
    override val root = VBox()



    companion object handler {
        var path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "accounttool"
        var customDir = File(path)

        var accounts = FXCollections.observableArrayList<Account>(

        )
        var gson = GsonBuilder().setPrettyPrinting().create()
        val ggson = Gson()



        fun writeData(){
            var writeFile = File(path + File.separator + "accounts.json")
            FileWriter(writeFile).use{
                ggson.toJson(accounts, it)
            }
        }

        fun readData(){
            accounts.clear()
            var readFile = File(path + File.separator + "accounts.json")
            if(!readFile.exists()) {
                readFile.createNewFile()
            }
            FileReader(readFile).use {
                var account = gson.fromJson(it, Array<Account>::class.java) ?: return
                for (i in account) {
                    accounts.add(i)
                }
            }
        }
    }


    init {
        println(path)

        if (customDir.exists()) {
        } else if (customDir.mkdirs()) {
        } else {
        }

        FX.primaryStage.width = 500.0
        FX.primaryStage.height = 450.0
        FX.primaryStage.isResizable = false
        readData()
        borderpane {
            center {
                tableview<Account>{
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY;
                    items = accounts

                    column("Name", Account::name)
                    column("Login", Account::login)
                    column("Password", Account::password)

                    contextMenu = ContextMenu().apply{
                        menuitem("Delete"){
                            selectedItem?.apply{
                                accounts.remove(selectedItem)
                                writeData()
                                readData()
                            }
                        }
                    }
                }
            }
            bottom{
                button("Add account").setOnAction{
                    replaceWith(AddView::class, ViewTransition.SlideIn)
                }
            }
        }
    }
}

class AddView: View(){
    override val root = VBox()

    var appField: TextField by singleAssign()
    var loginField: TextField by singleAssign()
    var passField: TextField by singleAssign()

    init {
        label("Application name")
        appField = textfield()
        label("Login")
        loginField = textfield()
        label("Password")
        passField = textfield()

        button("Add").setOnAction {
            ToolView.accounts.add(Account(appField.text, loginField.text, passField.text))
            ToolView.writeData()
            ToolView.readData()
            replaceWith(ToolView::class, ViewTransition.SlideIn)
        }
    }
}


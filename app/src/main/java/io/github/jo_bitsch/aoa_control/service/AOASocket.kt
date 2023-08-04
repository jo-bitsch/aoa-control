package io.github.jo_bitsch.aoa_control.service

import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.SyncFailedException

class AOASocket(private val fd: FileDescriptor) {
    val inputStream = object : InputStream() {
        private val bis = FileInputStream(fd).buffered()
        override fun read(): Int {
            return bis.read()
        }
    }
    val outputStream = object :  FileOutputStream(fd) {
        override fun flush() {
            try {
                fd.sync()
            } catch (e: SyncFailedException){
                // we don't need the guarantee that the sync worked, only the best effort :-)
            }
        }
    }

}



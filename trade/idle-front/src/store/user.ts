import { defineStore } from 'pinia'

export const useUserStore = defineStore("user",{
  state(){
    return{
      userId:0,
      username:"",
      avatar:""
    }
  },
  actions:{
    setUserInfo(data:any){
      this.userId = data.id
      this.username = data.username
      this.avatar = data.avatar
    }
  }
})
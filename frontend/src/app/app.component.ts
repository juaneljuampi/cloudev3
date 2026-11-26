import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { environment } from '../environments/environment';

interface Pedido { id:number; cliente:string; producto:string; cantidad:number; total:number; estado:string; creado:string }
@Component({selector:'app-root',standalone:true,imports:[CommonModule,FormsModule],template:`
<main class="shell"><header><div class="brand">P<span>360</span></div><div><small>GESTIÓN DE VENTAS</small><h1>Pedidos 360</h1></div><button *ngIf="!authenticated" class="primary" (click)="login()">Ingresar con IDaaS</button><button *ngIf="authenticated" class="quiet" (click)="logout()">Cerrar sesión</button></header>
<section class="hero"><div><p class="eyebrow">PANEL DE OPERACIONES</p><h2>Hola, {{userName || 'equipo'}} 👋</h2><p>Revisa y registra pedidos en un solo lugar.</p></div><div class="stat"><span>Pedidos registrados</span><strong>{{orders.length}}</strong></div></section>
<section class="content" *ngIf="authenticated; else signin"><form (ngSubmit)="create()"><h3>Nuevo pedido</h3><div class="fields"><label>Cliente<input name="cliente" [(ngModel)]="form.cliente" required placeholder="Nombre del cliente"></label><label>Producto<input name="producto" [(ngModel)]="form.producto" required placeholder="Producto"></label><label>Cantidad<input name="cantidad" [(ngModel)]="form.cantidad" type="number" min="1" required></label><label>Total ($)<input name="total" [(ngModel)]="form.total" type="number" min="1" required></label></div><button class="primary" type="submit" [disabled]="busy">{{busy?'Guardando...':'Crear pedido'}}</button><p class="message" *ngIf="message">{{message}}</p></form>
<section class="list"><div class="list-head"><h3>Pedidos recientes</h3><button class="quiet" (click)="load()">Actualizar ↻</button></div><p class="empty" *ngIf="!orders.length">Todavía no hay pedidos. Crea el primero arriba.</p><article class="order" *ngFor="let o of orders"><div class="order-icon">↗</div><div class="order-main"><strong>{{o.producto}}</strong><small>{{o.cliente}} · {{o.cantidad}} unidad(es)</small></div><span class="pill">{{o.estado}}</span><b>{{o.total | currency:'CLP':'symbol-narrow':'1.0-0'}}</b></article></section></section>
<ng-template #signin><section class="signin"><div class="lock">✦</div><h3>Acceso seguro</h3><p>Inicia sesión con el proveedor institucional para ver y gestionar pedidos.</p><button class="primary" (click)="login()">Iniciar sesión</button></section></ng-template><footer>Pedidos 360 <span>·</span> Eventos de pedido con Kafka</footer></main>`})
export class AppComponent implements OnInit {
  private oidc=inject(OidcSecurityService); private http=inject(HttpClient);
  authenticated=false; userName=''; orders:Pedido[]=[]; busy=false; message='';
  form={cliente:'',producto:'',cantidad:1,total:0};
  ngOnInit(){this.oidc.isAuthenticated$.subscribe(({isAuthenticated,userData})=>{this.authenticated=isAuthenticated;this.userName=userData?.name??'';if(isAuthenticated)this.load();});}
  login(){this.oidc.authorize();} logout(){this.oidc.logoff().subscribe();}
  load(){this.http.get<Pedido[]>(`${environment.apiUrl}/pedidos`).subscribe({next:x=>this.orders=x.reverse(),error:()=>this.message='No se pudieron cargar pedidos. Verifica API y token.'});}
  create(){this.busy=true;this.message='';this.http.post<Pedido>(`${environment.apiUrl}/pedidos`,this.form).subscribe({next:()=>{this.form={cliente:'',producto:'',cantidad:1,total:0};this.message='Pedido creado correctamente.';this.busy=false;this.load();},error:()=>{this.message='No se pudo guardar el pedido.';this.busy=false;}});}
}

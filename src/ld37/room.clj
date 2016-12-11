(ns ld37.room
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.graphics.glutils ShapeRenderer
                                              ShapeRenderer$ShapeType))
  (:require [ld37.common :as c]))

(defn make-tile-actor
  []
  (let [shape-renderer (ShapeRenderer.)]
    (proxy [Actor] []
      (draw [batch parent-alpha]
        (.end batch)
        (.setProjectionMatrix shape-renderer (.getProjectionMatrix batch))
        (.setTransformMatrix shape-renderer (.getTransformMatrix batch))
        (.translate shape-renderer (.getX this) (.getY this) 0)
        (.setColor shape-renderer 1 1 1 parent-alpha)
        (.begin shape-renderer ShapeRenderer$ShapeType/Line)
        (.rect shape-renderer 0 0 c/tile-width c/tile-width)
        (.end shape-renderer)
        (.begin batch)))))

(defn make-room-actor
  [{:keys [width height] :as game}]
  (let [floor (proxy [Group] []
                )
        tiles (doall (for [x (range width)
                           y (range height)]
                       (let [tile (make-tile-actor)]
                         (.setPosition tile
                                       (* c/tile-width x)
                                       (* c/tile-width y))
                         (.addActor floor tile)
                         tile)))]
    floor))

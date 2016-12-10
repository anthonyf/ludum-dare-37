(ns ld37.room
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.graphics.glutils ShapeRenderer
                                              ShapeRenderer$ShapeType)))

(def tile-width 64)

(defn make-tile-actor
  []
  (let [shape-renderer (ShapeRenderer.)]
    (proxy [Actor] []
      (draw [batch parent-alpha]
        (.setProjectionMatrix shape-renderer (.getProjectionMatrix batch))
        (.setTransformMatrix shape-renderer (.getTransformMatrix batch))
        (.translate shape-renderer (.getX this) (.getY this) 0)
        (.begin shape-renderer ShapeRenderer$ShapeType/Line)
        (.setColor shape-renderer 1.0 0.0 0.0 parent-alpha)
        (.rect shape-renderer 0 0 tile-width tile-width)
        (.end shape-renderer)))))

(defn make-room-actor
  [{:keys [width height] :as game}]
  (let [floor (proxy [Group] []
                )
        tiles (doall (for [x (range width)
                           y (range height)]
                       (let [tile (make-tile-actor)]
                         (.setPosition tile
                                       (* tile-width x)
                                       (* tile-width y))
                         (.addActor floor tile)
                         tile)))]
    floor))

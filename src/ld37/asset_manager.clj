(ns ld37.asset-manager
  (:import [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx.graphics.g2d GlyphLayout]
           [com.badlogic.gdx.assets AssetManager]
           [com.badlogic.gdx.assets.loaders.resolvers InternalFileHandleResolver]
           [com.badlogic.gdx.graphics.g2d.freetype
            FreeTypeFontGenerator FreeTypeFontGeneratorLoader FreetypeFontLoader
            FreetypeFontLoader$FreeTypeFontLoaderParameter]
           (com.badlogic.gdx.graphics.g2d BitmapFont TextureRegion NinePatch)
           (com.badlogic.gdx.scenes.scene2d.ui Image ImageTextButton
                                               ImageTextButton$ImageTextButtonStyle)
           (com.badlogic.gdx.utils Scaling Align)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable NinePatchDrawable)))

(defn setup-asset-manager []
  (let [manager (AssetManager.)
        resolver (InternalFileHandleResolver.)]
    (.setLoader manager FreeTypeFontGenerator (FreeTypeFontGeneratorLoader. resolver))
    (.setLoader manager BitmapFont ".ttf" (FreetypeFontLoader. resolver))
    manager))

(def manager (setup-asset-manager))

(defn make-texture-drawable
  [name]
  (TextureRegionDrawable. (TextureRegion. (.get manager name Texture))))

(defn make-texture-drawable-flip
  [name flip-x? flip-y?]
  (let [tr (TextureRegion. (.get manager name Texture))]
    (.flip tr flip-x? flip-y?)
    (TextureRegionDrawable. tr)))
